package com.github.gv2011.http.imp.acme;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.toISet;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.time.Instant;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.FileUtils;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.http.AcmeStore;
import com.github.gv2011.util.http.DomainEntry;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.RsaKeyPair;
import com.github.gv2011.util.sec.SecUtils;
import com.github.gv2011.util.sec.ServerCertificate;

public final class AcmeFileStore implements AcmeStore{
  
  private static final URI ACME_STAGING_URL = URI.create("acme://letsencrypt.org/staging");
  private static final String CRT_PATTERN = ".{}.crt";
  private static final String RSA_EXT = ".rsa";
  private static final String LAST_ERROR_TXT = "lastError.txt";
  
  private final Path dir;
  private final Path acmeUrlFile;
  private final Path accountFile;
  private final Path domainsDir;
  
  public AcmeFileStore(Path dir) {
    this.dir = dir;
    acmeUrlFile = dir.resolve("acme-url.txt");
    accountFile = dir.resolve("account-url.txt");
    domainsDir = dir.resolve("domains");
    call(()->Files.createDirectories(domainsDir));
    if(!Files.exists(acmeUrlFile)){
      FileUtils.writeText(ACME_STAGING_URL.toString(), acmeUrlFile);
    }
  }

  @Override
  public URI acmeUrl() {
    return URI.create(ByteUtils.read(acmeUrlFile).utf8ToString().trim());
  }
  
  
  @Override
  public boolean production() {
    return !acmeUrl().equals(ACME_STAGING_URL);
  }

  @Override
  public RsaKeyPair userKeyPair() {
    final Path keyFile = dir.resolve("userKey.rsa");
    if(Files.exists(keyFile)) return RsaKeyPair.parsePkcs8(ByteUtils.read(keyFile));
    else{
      final RsaKeyPair key = RsaKeyPair.create();
      key.encode().write(keyFile, true);
      return key;
    }
  }

  @Override
  public Opt<URI> accountUrl() {
    return Files.exists(accountFile) ? Opt.of(URI.create(ByteUtils.read(accountFile).utf8ToString().trim())) : Opt.empty();
  }
  
  @Override
  public void setAccountUrl(URI url) {
    FileUtils.writeText(url.toString(), accountFile);
  }

  @Override
  public DomainEntry getEntry(Domain host) {
    final Path dir = domainsDir.resolve(host.toAscii());
    Path keyFile = dir.resolve(host+RSA_EXT);
    final RsaKeyPair key;
    if(!Files.exists(keyFile)){
      call(()->Files.createDirectories(dir));
      key = RsaKeyPair.create();
      key.encode().write(keyFile, true);
    }
    else{
      key = RsaKeyPair.parsePkcs8(ByteUtils.read(keyFile));
    }
    String certFilePattern = host+CRT_PATTERN;
    final IList<X509Certificate> chain = SecUtils.readCertificateChain(dir, certFilePattern);
    
    Path errFile = dir.resolve(LAST_ERROR_TXT);
    Opt<Instant> lastError = Files.exists(errFile) 
      ? Opt.of(Instant.parse(ByteUtils.read(errFile).utf8ToString().trim()))
      : Opt.empty()
    ;
    
    return BeanUtils.beanBuilder(DomainEntry.class)
      .set(DomainEntry::domain).to(host)
      .set(DomainEntry::key).to(key)
      .set(DomainEntry::certificateChain).to(chain)
      .set(DomainEntry::lastError).to(lastError)
      .build()
    ;
  }
  
  @Override
  public void setError(Domain domain) {
    FileUtils.writeText(Instant.now().toString(), domainsDir.resolve(domain.toAscii()).resolve(LAST_ERROR_TXT));    
  }


  @Override
  public void add(ServerCertificate serverCertificate) {
    Domain domain = serverCertificate.domain();
    final Path dir = domainsDir.resolve(domain.toString());
    call(()->Files.createDirectories(dir));
    String certFilePattern = domain+CRT_PATTERN;
    SecUtils.writeCertificateChain(serverCertificate.certificateChain(), dir, certFilePattern);
  }

  @Override
  public ISet<Domain> availableDomains() {
    return callWithCloseable(()->Files.list(domainsDir), dirs->(ISet<Domain>) dirs
      .filter(d->Files.exists(d.resolve(d.getFileName()+format(CRT_PATTERN, "01"))))
      .map(d->Domain.parse(d.getFileName().toString()))
      .collect(toISet())
    );
  }

  @Override
  public void close() {}
}
