package com.github.gv2011.http.imp.server;

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static org.slf4j.LoggerFactory.getLogger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;

import javax.security.auth.x500.X500Principal;

import org.slf4j.Logger;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.Store;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.http.CertificateHandler;
import com.github.gv2011.util.http.CertificateUpdate;
import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.RsaKeyPair;
import com.github.gv2011.util.sec.SecProvider;
import com.github.gv2011.util.sec.SecUtils;
import com.github.gv2011.util.sec.ServerCertificate;
import com.github.gv2011.util.time.Clock;

public final class SelfSignedCertificateHandler implements CertificateHandler{

  private static final Logger LOG = getLogger(SelfSignedCertificateHandler.class);

  private static final Path KEY_FILE = Paths.get("key.pkcs8");
  private static final Path CERT_FILE = Paths.get("cert.der");
  private static final Path CERT_FILE_PEM = Paths.get("cert.pem");


  private final Domain domain;
  private final Store<X509Certificate> certStore;
  private final RsaKeyPair caKey;
  private final X509Certificate caCert;
  private final RsaKeyPair serverKey;

  private final Object lock = new Object();
  private AutoCloseableNt updaterTask;
  private boolean closed;

  private volatile ServerCertificate certificate;
  private volatile Consumer<CertificateUpdate> updater = _->{};


  public SelfSignedCertificateHandler(final Domain domain) {
    this(
      domain,
      new Store<RsaKeyPair>(){
        @Override
        public Opt<RsaKeyPair> tryRead() {
          return ByteUtils.tryRead(KEY_FILE).map(RsaKeyPair::parsePkcs8);
        }
        @Override
        public void store(final RsaKeyPair key) {
          key.encode().write(KEY_FILE);
        }
      },
      new Store<X509Certificate>(){
        @Override
        public Opt<X509Certificate> tryRead() {
          return ByteUtils.tryRead(CERT_FILE).map(SecUtils::readCertificate);
        }
        @Override
        public void store(final X509Certificate certificate) {
          ByteUtils.newBytes(call(certificate::getEncoded)).write(CERT_FILE);
          SecUtils.convertToPem(certificate).write(CERT_FILE_PEM);
        }
      }
    );
  }

  public SelfSignedCertificateHandler(final Domain domain, final Store<RsaKeyPair> keyStore, final Store<X509Certificate> certStore) {
    this.domain = domain;
    caKey = readOrCreateKey(keyStore);
    this.certStore = certStore;
    caCert = readOrCreateCaCert(caKey, certStore);

    serverKey = RsaKeyPair.create();
    certificate = createServerCertificate(domain, caCert.getSubjectX500Principal(), caKey, serverKey);
    LOG.info(
      "Created certificate {}",
      ByteUtils.newBytes(call(certificate.certificateChain().leafCertificate()::getEncoded)).hash().toString()
    );

    registerUpdaterTask();
  }


  @Override
  public ISet<Domain> availableDomains() {
    return ICollections.setOf(domain);
  }

  @Override
  public Opt<ServerCertificate> getCertificate(final Domain host, final Consumer<CertificateUpdate> updater) {
    SelfSignedCertificateHandler.this.updater = updater;
    return host.equals(domain) ? Opt.of(certificate) : Opt.empty();
  }

  private void registerUpdaterTask() {
    synchronized(lock){
      if(!closed){
        final Instant expiry = certificate.certificateChain().leafCertificate().getNotAfter().toInstant();
        final Instant updateTime = updateTime(expiry);
        updaterTask = Clock.INSTANCE.get().runAt(
          this::updateCertificate,
          updateTime
        );
        LOG.info("Registered certificate update at {} (expires at {}).", updateTime, expiry);
      }
    }
  }

  private Instant updateTime(Instant instant) {
    Instant now = Clock.get().instant();
    return now.plus(verify(Duration.between(now, instant).dividedBy(2), d->d.isPositive()));
  }

  private static X509Certificate readOrCreateCaCert(final RsaKeyPair caRsaKey, final Store<X509Certificate> certStore) {
    return certStore.tryRead()
      .orElseGet(()->{
        final X509Certificate c = SecProvider.instance().createCertificateBuilder()
          .setCa(true)
          .build(caRsaKey)
        ;
        certStore.store(c);
        return c;
      })
    ;
  }

  private static RsaKeyPair readOrCreateKey(final Store<RsaKeyPair> keyStore) {
    return keyStore.tryRead()
      .orElseGet(()->{
        final RsaKeyPair k = RsaKeyPair.create();
        keyStore.store(k);
        return k;
      })
    ;
  }


  @Override
  public X509Certificate rootCertificate() {
    return certStore.tryRead().get();
  }

  private static ServerCertificate createServerCertificate(Domain domain, X500Principal caPrincipal, RsaKeyPair caKey, final RsaKeyPair serverKey) {
    return BeanUtils.beanBuilder(ServerCertificate.class)
      .set(ServerCertificate::domain).to(domain)
      .set(ServerCertificate::keyPair).to(serverKey)
      .set(ServerCertificate::certificateChain)
      .to(SecUtils.createCertificateChain(ICollections.listOf(
        createX509CertificateServerCertificate(domain, caPrincipal, caKey, serverKey.getPublic())
      )))
      .build()
    ;
  }

  private static X509Certificate createX509CertificateServerCertificate(Domain domain, X500Principal caPrincipal, RsaKeyPair caKey, final RSAPublicKey serverKey) {
    return SecProvider.instance().createCertificateBuilder()
      .setDomains(pair(domain, ICollections.emptySortedSet()))
      .setSubjectPublicKey(serverKey)
      .setIssuer(caPrincipal)
      .build(caKey)
    ;
  }

  private void updateCertificate(){
    LOG.info("Updating certificate.");
    certificate = createServerCertificate(domain, caCert.getSubjectX500Principal(), caKey, serverKey);
    updater.accept(
      BeanUtils.beanBuilder(CertificateUpdate.class)
      .set(CertificateUpdate::newCertificate).to(certificate)
      .build()
    );
    registerUpdaterTask();
  }

  @Override
  public void close() {
    updater = _->{};
    synchronized(lock){
      closed = true;
      updaterTask.close();
    }
  }

}
