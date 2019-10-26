package com.github.gv2011.util.sec;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import static com.github.gv2011.util.NumUtils.withLeadingZeros;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.icol.ICollections.listBuilder;
import static com.github.gv2011.util.icol.ICollections.listOf;
import static com.github.gv2011.util.icol.ICollections.toIList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gv2011.util.FileUtils;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.bytes.BytesBuilder;
import com.github.gv2011.util.bytes.Hash256;
import com.github.gv2011.util.ex.ThrowingConsumer;
import com.github.gv2011.util.ex.ThrowingSupplier;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Opt;

public final class SecUtils {

  public static final String TLSV12 = "TLSv1.2";
  public static final String JKS = "JKS";
  public static final String RSA = "RSA";
  public static final String JKS_DEFAULT_PASSWORD = "changeit";
  public static final String KEY_FILE_NAME = "key.pkcs8";
  public static final String PKCS12 = "PKCS12";
  public static final String PKCS12_FILE_EXTENSION = "p12";
  public static final String JAVAX_NET_DEBUG_SYS_PROP = "javax.net.debug";
  public static final String JAVAX_NET_DEBUG_SYS_PROP_ALL = "all";

  private static final String PKIX = "PKIX";
  private static final String SUN_X509 = "SunX509";
  private static final String CERT_FILE_PATTERN = "cert{}.crt";
  private static final String CERT_ALIAS = "cert";
  private static final String X_509 = "X.509";

  private SecUtils(){staticClass();}

  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(SecUtils.class);

  public static RSAPublicKey createRsaPublicKey(final BigInteger modulus, final BigInteger publicExponent){
    return (RSAPublicKey) call(()->
      KeyFactory.getInstance(RSA).generatePublic(new RSAPublicKeySpec(modulus, publicExponent))
    );
  }

  public static final X509Certificate readCertificate(final Bytes bytes){
    final CertificateFactory certFactory = call(()->CertificateFactory.getInstance(X_509));
    return callWithCloseable(
      bytes::openStream,
      s->(X509Certificate)certFactory.generateCertificate(s)
    );
  }

  public static final Hash256 getFingerPrint(final Certificate certificate){
    return ByteUtils.newBytes(call(certificate::getEncoded)).hash();
  }

  public static final X509Certificate readCertificateFromPem(final String pem){
    final CertificateFactory certFactory = call(()->CertificateFactory.getInstance(X_509));
    return callWithCloseable(
      ()->new ByteArrayInputStream(pem.getBytes(StandardCharsets.US_ASCII)),
      s->(X509Certificate)certFactory.generateCertificate(s)
    );
  }

  public static final IList<X509Certificate> readCertificateChainFromPem(final String pem){
    final CertificateFactory certFactory = call(()->CertificateFactory.getInstance(X_509));
    return callWithCloseable(
      ()->new ByteArrayInputStream(pem.getBytes(StandardCharsets.US_ASCII)),
      s->{
        final IList.Builder<X509Certificate> b = listBuilder();
        b.add((X509Certificate)certFactory.generateCertificate(s));
        b.add((X509Certificate)certFactory.generateCertificate(s));
        return b.build();
      }
    );
  }

  public static final void writeCertificateChain(final IList<X509Certificate> certChain, final Path folder){
    int i=0;
    boolean done = false;
    while(!done){
      final Path certFile = certFile(folder, i);
      final boolean deleted = FileUtils.deleteFile(certFile);
      if(!deleted && i>=certChain.size()) done=true;
      i++;
    }
    for(i=0; i<certChain.size(); i++){
      final X509Certificate cert = certChain.get(i);
      ByteUtils.newBytes(call(cert::getEncoded)).write(certFile(folder,i));
    }
  }

  public static final Bytes convertToPkcs12(final Path folder){
    final RsaKeyPair keyPair = RsaKeyPair.parse(ByteUtils.read(folder.resolve("key.rsa")));
    final IList<X509Certificate> chain = readCertificateChain(folder);
    return call(()->{
      final KeyStore ks = KeyStore.getInstance(SecUtils.PKCS12);
      ks.load(null, null);
      ks.setKeyEntry(
        CERT_ALIAS,
        keyPair.getPrivate(),
        JKS_DEFAULT_PASSWORD.toCharArray(),
        chain.toArray(new Certificate[chain.size()])
      );
      final BytesBuilder bytesBuilder = ByteUtils.newBytesBuilder();
      ks.store(bytesBuilder, "default".toCharArray());
      return bytesBuilder.build();
    });
  }

  public static final IList<X509Certificate> readCertificateChain(final Path folder){
    final IList.Builder<X509Certificate> chain = listBuilder();
    int i = 0;
    Path certFile = certFile(folder, i);
    while(Files.exists(certFile)){
      chain.add(readCertificate(ByteUtils.read(certFile)));
      certFile = certFile(folder, ++i);
    }
    return chain.build();
  }

  private static Path certFile(final Path folder, final int i) {
    return folder.resolve(format(CERT_FILE_PATTERN,withLeadingZeros(i+1,2)));
  }


  public static final KeyStore readKeyStore(final ThrowingSupplier<InputStream> streamSupplier){
    final KeyStore ks = call(()->KeyStore.getInstance(JKS));
    callWithCloseable(
      streamSupplier,
      (ThrowingConsumer<InputStream>)s->ks.load(s, JKS_DEFAULT_PASSWORD.toCharArray())
    );
    return ks;
  }

  public static final KeyStore createJKSKeyStore(
    final RsaKeyPair privKey, final IList<X509Certificate> certChain
  ){
    final KeyStore keyStore = call(()->KeyStore.getInstance(JKS));
    call(()->keyStore.load(null));
    return addToKeyStore(privKey, certChain, keyStore, Opt.empty());
  }

  public static final KeyStore createJKSKeyStore(final Path certificateDirectory){
    return createJKSKeyStore(
      RsaKeyPair.parse(ByteUtils.read(certificateDirectory.resolve(KEY_FILE_NAME))),
      listOf(readCertificate(ByteUtils.read(certFile(certificateDirectory, 0))))
    );
  }

  public static final KeyStore addToKeyStore(
    final RsaKeyPair privKey, final IList<X509Certificate> certChain, final KeyStore keystore, final Opt<String> alias
  ){
    final X509Certificate cert = certChain.get(0);
    verifyEqual(privKey.getPublic(), cert.getPublicKey());
    call(()->keystore.setKeyEntry(
      alias.orElseGet(()->findAlias(keystore)),
      privKey.getPrivate(),
      JKS_DEFAULT_PASSWORD.toCharArray(),
      certChain.toArray(new Certificate[certChain.size()])
    ));
    return keystore;
  }

  private static final String findAlias(final KeyStore ks){
    return call(()->{
      String alias = CERT_ALIAS;
      int i=0;
      while(ks.containsAlias(alias)) {
        alias = CERT_ALIAS+(++i);
      }
      return alias;
    });
  }

  public static final Bytes createJKSKeyStoreBytes(
    final RsaKeyPair privKey, final IList<X509Certificate> certChain
  ){
    Bytes result;
    try(BytesBuilder builder = ByteUtils.newBytesBuilder()){
      call(()->createJKSKeyStore(privKey, certChain).store(builder, JKS_DEFAULT_PASSWORD.toCharArray()));
      result = builder.build();
    }
    return result;
  }

  public static final Bytes createJKSKeyStore(final X509Certificate trustedCertificate){
    final KeyStore keystore = call(()->KeyStore.getInstance(JKS));
    call(()->keystore.load(null, null));
    call(()->keystore.setCertificateEntry(CERT_ALIAS, trustedCertificate));
    Bytes result;
    try(BytesBuilder builder = ByteUtils.newBytesBuilder()){
      call(()->keystore.store(builder, JKS_DEFAULT_PASSWORD.toCharArray()));
      result = builder.build();
    }
    return result;
  }

  public static final void extractJKSKeyStore(final Bytes keyStore, final Path folder){
    final KeyStore ks = readKeyStore(keyStore::openStream);
    final RsaKeyPair privKey = RsaKeyPair.create(
      (RSAPrivateCrtKey)call(()->ks.getKey(CERT_ALIAS, JKS_DEFAULT_PASSWORD.toCharArray()))
    );
    final IList<X509Certificate> chain = Arrays.stream(call(()->ks.getCertificateChain(CERT_ALIAS)))
      .map(c->(X509Certificate)c)
      .collect(toIList())
    ;
    privKey.encode().write(folder.resolve(KEY_FILE_NAME));
    writeCertificateChain(chain, folder);
  }

  public static final SSLServerSocketFactory createServerSocketFactory(final Bytes keyStore){
    return createServerSocketFactory(readKeyStore(keyStore::openStream), false);
  }

  public static final SSLServerSocketFactory createServerSocketFactory(
    final KeyStore keyStore, final boolean trustAll
  ){
    return call(()->{
      final SSLContext sslContext = call(()->SSLContext.getInstance(TLSV12));
      sslContext.init(
        getKeyManagers(keyStore),
        trustAll ? new TrustManager[]{new TrustAllTrustManager()} : null,
        null
      );
      return sslContext.getServerSocketFactory();
    });
  }

  private static final KeyManager[] getKeyManagers(final KeyStore keyStore){
    return call(()->{
      final KeyManagerFactory kmf = KeyManagerFactory.getInstance(SUN_X509);
      kmf.init(keyStore, SecUtils.JKS_DEFAULT_PASSWORD.toCharArray());
      return kmf.getKeyManagers();
    });
  }

  public static final SSLServerSocketFactory createServerSocketFactory(final Path certificateDirectory){
    return createServerSocketFactory(certificateDirectory, false);
  }

  public static final SSLServerSocketFactory createServerSocketFactory(
    final Path certificateDirectory, final boolean trustAll
  ){
    createCertificateIfMissing(certificateDirectory);
    return createServerSocketFactory(
      createJKSKeyStore(certificateDirectory),
      trustAll
    );
  }

  private static final void createCertificateIfMissing(final Path certificateDirectory){
    call(()->{
      Files.createDirectories(certificateDirectory);
      final Path keyFile = certificateDirectory.resolve(KEY_FILE_NAME);
      final Path certFile = certFile(certificateDirectory, 0);
      if(!Files.exists(keyFile)){
        verify(!Files.exists(certFile));
        RsaKeyPair.create().encode().write(keyFile);
      }
      if(!Files.exists(certFile)){
        ByteUtils.newBytes(
          CertificateBuilder.create().build(RsaKeyPair.parse(ByteUtils.read(keyFile))).getEncoded()
        ).write(certFile);
      }
    });
  }

  public static final SSLSocket connect(
    final Path certificateDirectory, final PublicKey peer, final InetSocketAddress address
  ){
    createCertificateIfMissing(certificateDirectory);
    return call(()->{
      final SSLContext sslContext = call(()->SSLContext.getInstance(TLSV12));
      sslContext.init(
        getKeyManagers(createJKSKeyStore(certificateDirectory)),
        new TrustManager[]{new TrustAllTrustManager()},
        null
      );
      boolean success = false;
      final Socket s = sslContext.getSocketFactory().createSocket(address.getAddress(), address.getPort());
      try{
        final SSLSocket socket = (SSLSocket) s;
        verifyEqual(socket.getSession().getPeerCertificates()[0].getPublicKey(), peer);
        success = true;
        return socket;
      }
      finally{
        if(!success) s.close();
      }
    });
  }

  public static final SSLSocketFactory createSocketFactory(final X509Certificate trustedCertificate){
    return call(()->{
      final KeyStore ks = KeyStore.getInstance(JKS);
      ks.load(null, null);
      ks.setCertificateEntry("cert", trustedCertificate);
      final TrustManagerFactory tmf = TrustManagerFactory.getInstance(PKIX);
      tmf.init(ks);
      final SSLContext sslContext = call(()->SSLContext.getInstance(TLSV12));
      sslContext.init(null, tmf.getTrustManagers() , null);
      return sslContext.getSocketFactory();
    });
  }

  public static RSAPublicKey getPublicKey(final Path certificateDirectory) {
    createCertificateIfMissing(certificateDirectory);
    return RsaKeyPair.parse(ByteUtils.read(certificateDirectory.resolve(KEY_FILE_NAME))).getPublic();
  }

}
