package com.github.gv2011.http.imp.acme;

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.CollectionUtils.toSingle;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.closeAll;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.wrap;
import static com.github.gv2011.util.icol.ICollections.listFrom;
import static com.github.gv2011.util.icol.ICollections.pathOf;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static java.util.Comparator.comparing;
import static org.slf4j.LoggerFactory.getLogger;

import java.net.URL;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.AccountBuilder;
import org.shredzone.acme4j.Certificate;
import org.shredzone.acme4j.Order;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.Status;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.util.CSRBuilder;
import org.slf4j.Logger;

import com.github.gv2011.http.imp.AcmeAccess;
import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.StreamUtils;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.ex.ThrowingRunnable;
import com.github.gv2011.util.http.AcmeStore;
import com.github.gv2011.util.http.CertificateHandler;
import com.github.gv2011.util.http.CertificateUpdate;
import com.github.gv2011.util.http.DomainEntry;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.icol.Path;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.ServerCertificate;
import com.github.gv2011.util.time.Clock;
import com.github.gv2011.util.time.Poller;
import com.github.gv2011.util.time.TimeUtils;

public class AcmeCertHandler implements CertificateHandler, AutoCloseableNt{
  
  private static final Duration WAIT_AFTER_UPDATE_ERROR = Duration.ofDays(1);

  private static final Logger LOG = getLogger(AcmeCertHandler.class);
  
  private static final String WELL_KNOWN = ".well-known";
  private static final Path TOKEN_BASE_PATH = pathOf(WELL_KNOWN, "acme-challenge");
  
  private static final int BUCKET_CAPACITY = 50;
  private static final Duration BUCKET_INTERVAL = Duration.ofHours(4);
  
  private final AcmeStore store;
  private final Predicate<Domain> domainIsActive;
  private final Supplier<AcmeAccess> acmeAccess;
  private final Constant<Integer> tokenPort;
  private final boolean useBucket;
 
  private final Poller poller = TimeUtils.poller(Duration.ofSeconds(3), Opt.of(Duration.ofSeconds(60)));

  private final Clock clock;
  private final Thread updaterThread;
  private final Object lock = new Object();
  
  private int bucket = BUCKET_CAPACITY / 2;
  private Instant lastBucketFill = Instant.now();
  
  private boolean closed;




  public AcmeCertHandler(
    Clock clock, 
    AcmeStore store,
    Predicate<Domain> domainIsActive,
    Supplier<AcmeAccess> acmeAccess, 
    final Constant<Integer> tokenPort, 
    boolean useBucket
  ) {
    this.clock = clock;
    this.store = store;
    this.domainIsActive = domainIsActive;
    this.acmeAccess = acmeAccess;
    this.tokenPort = tokenPort;
    this.useBucket = useBucket;
    updaterThread = new Thread(this::update, "acme-updater");
  }
  
  public void start(){
    updaterThread.start();
  }

  @Override
  public ISet<Domain> availableDomains() {
    return store.availableDomains().stream().filter(this::domainIsActive).collect(toISet());
  }

  @Override
  public Opt<ServerCertificate> getCertificate(Domain host, Consumer<CertificateUpdate> updater) {
    return getCertificate(host, updater, true);
  }
    
  public Opt<ServerCertificate> getCertificate(Domain host, Consumer<CertificateUpdate> updater, boolean create) {
    if(!domainIsActive(host)){
      LOG.info("Host {} is not active.", host);
      return Opt.empty();
    }
    else{
      verify(!host.isInetAddress() && !host.isLocalhost(), ()->format("Host {} not supported.", host));
      synchronized(lock){
        try{
          final Opt<ServerCertificate> result;
          final DomainEntry entry = store.getEntry(host);
              
          if(!entry.certificateChain().isEmpty()) {
            result = Opt.of(BeanUtils.beanBuilder(ServerCertificate.class)
              .set(ServerCertificate::domain).to(host)
              .set(ServerCertificate::keyPair).to(entry.key())
              .set(ServerCertificate::certificateChain).to(entry.certificateChain())
              .build()
            );
            LOG.info("Certificate for {} exists.", host);
          }
          else if(!create){
            result = Opt.empty();
            LOG.info("Certificate for {} does not exist - should not create one.", host);
          }
          else {
            LOG.info("Creating certificate for {}.", entry.domain());
            if(useBucket){checkBucket();}
            result = Opt.of(orderCertificate(entry));
          }
          return result;
        }catch(Throwable t){
          LOG.error("Could not get certificate.", t);
          throw t;
        }finally{
          lock.notifyAll();
        }
      }
    }
  }
  
  private boolean domainIsActive(Domain domain){
    final boolean active = domainIsActive.test(domain);
    if(!active) LOG.warn("Domain {} is inactive.", domain);
    return active;
  }

  private void checkBucket() {
    assert Thread.holdsLock(lock);
    fillBucket();
    verify(bucket>0, ()->format("Bucket is empty."));
    bucket--;
    if(bucket==0) LOG.warn("Bucket is empty.");
    else LOG.info("Bucket: {} left.", bucket);
  }
  
  private void fillBucket(){
    assert Thread.holdsLock(lock);
    final Instant now = Instant.now();
    final int before = bucket;
    while(lastBucketFill.plus(BUCKET_INTERVAL).isBefore(now) && bucket<BUCKET_CAPACITY){
      lastBucketFill = lastBucketFill.plus(BUCKET_INTERVAL);
      bucket++;
    }
    if(bucket!=before) LOG.info("Bucket increased to: {} (bucket time: {}).", bucket, lastBucketFill);
  }

  private ServerCertificate orderCertificate(DomainEntry entry) {
    assert Thread.holdsLock(lock);
    checkTokenCanBeSet(entry.domain());
    try{
      final Account account = openAccount();
      final Order order = call(()->(Order)account.newOrder().domains(entry.domain().toAscii()).create());
      LOG.info("Created new order {}.", order.getLocation());
      
      final Http01Challenge challenge = order.getAuthorizations().stream()
        .filter(a->!a.getStatus().equals(Status.READY))
        .flatMap(a->Opt.ofNullable(a.findChallenge(Http01Challenge.class)).stream())
        .collect(toSingle())
      ;
      
      verify(!challenge.getStatus().equals(Status.READY));
      LOG.info(
        "Received challenge {} with status {}, token {} and content {}.", 
        challenge.getLocation(), challenge.getStatus(), challenge.getToken(), challenge.getAuthorization()
      );
      
      try(final AutoCloseableNt token = acmeAccess.get().activate(
        entry.domain(), 
        TOKEN_BASE_PATH.addElement(challenge.getToken()), 
        ByteUtils.asUtf8(challenge.getAuthorization())
      )){
      
        LOG.info("Activated token, triggering.");
        call(challenge::trigger);
        
        waitForSuccess("Challenge", challenge::getStatus, challenge::update);
      }
      LOG.info("Challenge successful.");
      
      CSRBuilder csrb = new CSRBuilder();
      csrb.addDomains(entry.domain().toAscii());
      call(()->csrb.sign(entry.key().asKeyPair()));
      
      call(()->order.execute(csrb.getEncoded()));
      waitForSuccess("Order", order::getStatus, order::update);
      
      Certificate certificate = order.getCertificate();
      LOG.info("Received certificate chain..");
      
      ServerCertificate result = BeanUtils.beanBuilder(ServerCertificate.class)
        .set(ServerCertificate::domain).to(entry.domain())
        .set(ServerCertificate::keyPair).to(entry.key())
        .set(ServerCertificate::certificateChain).to(listFrom(certificate.getCertificateChain()))
        .build()
      ;
            
      store.add(result);
      return result;
    }catch(Throwable t){
      store.setError(entry.domain());
      throw t;
    }
  }

  private void checkTokenCanBeSet(Domain domain) {
    final UUID random = UUID.randomUUID();
    final Path path = TOKEN_BASE_PATH.addElement(random.toString());
    final URL url = call(()->new URL("http://"+domain.toAscii()+":"+tokenPort.get()+"/"+path.urlEncoded()));
    final String content = random.toString();
    try(final AutoCloseableNt token = acmeAccess.get().activate(
      domain, 
      path, 
      ByteUtils.asUtf8(content)
    )){
      verifyEqual(
        StreamUtils.readText(url::openStream),
        content
      );
    }
    catch(Exception e){
      throw wrap(e, format("Setting token failed for {} at url {}.", domain, url));
    }
    LOG.info("Successfully checked token access for domain {} at url {}.", domain, url);
  }

  private Account openAccount() {
    final Session session = new Session(store.acmeUrl());
    final KeyPair userKeyPair = store.userKeyPair().asKeyPair();
  
    return store.accountUrl()
      .map(u->{
        final Account acc = session.login(call(u::toURL), userKeyPair).getAccount();
        LOG.info("Logged in to existing account {}.", acc.getLocation());
        return acc;
      })
      .orElseGet(()->{
        final Account newAccount = call(()->new AccountBuilder()
          .agreeToTermsOfService()
          .useKeyPair(userKeyPair)
          .create(session)
        );
        store.setAccountUrl(call(newAccount.getLocation()::toURI));
        LOG.info("Created new account {}.", newAccount.getLocation());
        return newAccount;
      })
    ;
  }

  private void waitForSuccess(String name, Supplier<Status> status, ThrowingRunnable update) {
    final Opt<Boolean> result = poller.poll(()->{
      update.run();
      final Status s = status.get();
      LOG.info("{} status is {}.", name, s);
      return
        s.equals(Status.VALID)   ? Opt.of(true ) :
        s.equals(Status.INVALID) ? Opt.of(false) :
        Opt.empty()
      ;
    });
    verifyEqual(result, Opt.of(true));
  }
  
  private void update(){
    boolean closed = false;
    while(!closed){
      synchronized(lock){
        try{
          Instant now = clock.instant();
          Opt<Pair<Instant, DomainEntry>> nextUpdate = store.availableDomains().stream()
            .filter(this::domainIsActive)
            .map(d->store.getEntry(d))
            .filter(e->!e.certificateChain().isEmpty())
            .map(e->pair(updateTime(e),e))
            .sorted(comparing(Pair::getKey))
            .tryFindFirst()
          ;
          if(nextUpdate.isEmpty()){
            LOG.info("No domains to update. Waiting.");
            call(()->lock.wait());
          }
          else{
            final Instant time = nextUpdate.get().getKey();
            DomainEntry entry = nextUpdate.get().getValue();
            if(!clock.hasPassed(time)){
              LOG.info("Next update is {} at {} (in {}).", entry.domain(), time, TimeUtils.approx(Duration.between(now, time)));
              clock.notifyAt(lock, time);
              call(()->lock.wait());
            }
            else{
              LOG.info("Updating {}.", entry.domain());
              orderCertificate(entry);
            }
          }
          closed = this.closed;
        }catch(Exception e){
          closed = this.closed;
          LOG.error(format("Error in updater."), e);
          if(!closed){
            clock.notifyAfter(lock, Duration.ofSeconds(30));
            call(()->lock.wait());
          }
        }
      }
    }
  }
  
  private Instant updateTime(DomainEntry entry){
    final X509Certificate cert = entry.certificateChain().first();
    final Instant expiryDate = cert.getNotAfter().toInstant();
    final Duration validityPeriod = Duration.between(cert.getNotBefore().toInstant(), expiryDate);
    verify(!validityPeriod.isNegative() && !validityPeriod.isZero());
    final Duration reserve = TimeUtils.min(validityPeriod.dividedBy(2), Duration.ofDays(15));
    final Instant updateTime = expiryDate.minus(reserve);
    return entry.lastError()
      .map(lastErrorDate->{
        //Don't repeat to frequently in case of errors:
        final Instant correctedUpdateTime = TimeUtils.latest(updateTime, lastErrorDate.plus(WAIT_AFTER_UPDATE_ERROR));
        if(correctedUpdateTime.isAfter(expiryDate.minus(WAIT_AFTER_UPDATE_ERROR))){
          LOG.warn("Expiry of {} due to errors imminent.", entry.domain());
        }
        return correctedUpdateTime;
      })
      .orElse(updateTime)
    ;
  }
  
  @Override
  public void close() {
    synchronized(lock){
      closed = true;
      lock.notifyAll();
    }
    closeAll(updaterThread::join, poller);
  }
  
}
