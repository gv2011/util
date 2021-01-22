package com.github.gv2011.http.imp.server;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.bug;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.http.HttpFactory.SERVER_SELECTS_PORT;
import static org.slf4j.LoggerFactory.getLogger;

import java.security.KeyStore;
import java.security.cert.CRL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Predicate;

import javax.net.ssl.TrustManager;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;

import com.github.gv2011.http.imp.AcmeAccess;
import com.github.gv2011.http.imp.HttpFactoryImp;
import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.http.CertificateHandler;
import com.github.gv2011.util.http.CertificateUpdate;
import com.github.gv2011.util.http.HttpServer;
import com.github.gv2011.util.http.RequestHandler;
import com.github.gv2011.util.http.Space;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.icol.Path;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.SecUtils;
import com.github.gv2011.util.sec.ServerCertificate;
import com.github.gv2011.util.sec.TrustAllTrustManager;

public class HttpServerImp implements HttpServer, AcmeAccess{
    
  private static final Logger LOG = getLogger(HttpServerImp.class);

  private final Server jetty;
  private final ServerConnector httpConnector;
  private final Opt<ServerConnector> httpsConnector;
  private final Opt<SslContextFactory.Server> sslContextFactory;
  private final Set<Domain> activeHttpsDomains = Collections.synchronizedSet(new HashSet<>());
  
  private final Opt<CertificateHandler> certHandler;
  private final Dispatcher dispatcher;

  public HttpServerImp(
    HttpFactoryImp http,
    IList<Pair<Space,RequestHandler>> handlers, 
    OptionalInt httpPort,
    Opt<CertificateHandler> certHandler,
    Predicate<Domain> isHttpsHost,
    OptionalInt httpsPort
  ) {
    this.certHandler = certHandler;
    jetty = new Server();
    assert jetty.getConnectors().length==0;
    
    final int effectiveHttpsPort = httpsPort.orElse(8443);
    final int effectiveHttpPort = httpPort.orElse(8080);
    
    if(certHandler.isPresent()){
      verify(effectiveHttpsPort==SERVER_SELECTS_PORT.getAsInt() || effectiveHttpsPort!=effectiveHttpPort);

      final HttpConfiguration config = new HttpConfiguration();
      config.setSendServerVersion(false);
      
      final HttpConnectionFactory plainConnFac = new HttpConnectionFactory(config);
      verifyEqual(plainConnFac.getProtocol(), "HTTP/1.1");
      
      sslContextFactory = Opt.of(createSslContextFactory(certHandler.get()));

      final SslConnectionFactory tlsConnFac = new SslConnectionFactory(sslContextFactory.get(), plainConnFac.getProtocol());
      
      final ServerConnector httpsConnector = new ServerConnector(jetty, tlsConnFac, plainConnFac);
      
      final int p = httpsPort.orElse(8443);
      httpsConnector.setPort(p);
      jetty.addConnector(httpsConnector);
      
      this.httpsConnector = Opt.of(httpsConnector);
      dispatcher = new Dispatcher(http, isHttpsHost, handlers, this::ensureActivated);
    }
    else{
      verify(httpsPort.isEmpty());
      httpsConnector = Opt.empty();
      sslContextFactory = Opt.empty();
      dispatcher = new Dispatcher(http, d->false, handlers, d->bug());
    }
    
    final HttpConfiguration config = new HttpConfiguration();
    config.setSendServerVersion(false);
    httpConnector = new ServerConnector(jetty, new HttpConnectionFactory(config));
    httpConnector.setPort(httpPort.orElse(8080));
    jetty.addConnector(httpConnector);
    
    jetty.setHandler(dispatcher);
    
    call(jetty::start);
  }

  private SslContextFactory.Server createSslContextFactory(CertificateHandler certificateHandler) {
    final KeyStore keyStore = call(()->(KeyStore)KeyStore.getInstance(SecUtils.JKS));
    call(()->keyStore.load(null));
    certificateHandler.availableDomains().forEach(d->{
      final Opt<ServerCertificate> cert = certHandler.get().getCertificate(d, this::updateCertificate);
      cert.ifPresent(c->SecUtils.addToKeyStore(c, keyStore));
    });
    
    final SslContextFactory.Server sslContextFactory = new SslContextFactory.Server(){
      @Override
      protected TrustManager[] getTrustManagers(KeyStore trustStore, Collection<? extends CRL> crls) {
        return new TrustManager[]{new TrustAllTrustManager()};
      }
    };
    sslContextFactory.setSniRequired(true);
    sslContextFactory.setKeyStore(keyStore);
    sslContextFactory.setKeyStorePassword(SecUtils.JKS_DEFAULT_PASSWORD);
    sslContextFactory.setWantClientAuth(true);
    return sslContextFactory;
  }

  @Override
  public void close() {
    call(jetty::stop);
    call(jetty::join);
  }
  
  

  @Override
  public OptionalInt httpsPort() {
    return httpsConnector.mapToInt(ServerConnector::getLocalPort);
  }

  @Override
  public int httpPort() {
    return httpConnector.getLocalPort();
  }
  
  
  private void ensureActivated(Domain domain){
    if(!activeHttpsDomains.contains(domain)){
      synchronized(activeHttpsDomains){
        if(!activeHttpsDomains.contains(domain)){
          LOG.debug("Activating https domain {}.", domain);
          final ServerCertificate cert = certHandler.get().getCertificate(domain, this::updateCertificate).get();
          call(()->{
            sslContextFactory.get().reload(scf->{
              SecUtils.addToKeyStore(cert, scf.getKeyStore());
            });
          });
          activeHttpsDomains.add(domain);
          LOG.warn("Activated https domain {}.", domain);
        }
      }
    } 
  }
  
  private void updateCertificate(final CertificateUpdate update){
    call(()->{
      sslContextFactory.get().reload(scf->{
        SecUtils.addToKeyStore(update.newCertificate(), scf.getKeyStore());
      });
    });
  }

  @Override
  public AutoCloseableNt activate(Domain host, Path tokenPath, TypedBytes token) {
    return dispatcher.activate(host, tokenPath, token);
  }
}
