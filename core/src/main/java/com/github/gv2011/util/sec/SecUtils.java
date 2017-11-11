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





import static com.github.gv2011.util.CollectionUtils.iCollections;
import static com.github.gv2011.util.CollectionUtils.toIList;
import static com.github.gv2011.util.NumUtils.withLeadingZeros;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.doWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.run;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyStore;
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
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gv2011.util.FileUtils;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.bytes.BytesBuilder;
import com.github.gv2011.util.ex.ThrowingSupplier;
import com.github.gv2011.util.icol.IList;

public final class SecUtils {

  private static final String PKIX = "PKIX";
  private static final String TLSV12 = "TLSv1.2";
  private static final String SUN_X509 = "SunX509";
  private static final String CERT_FILE_PATTERN = "cert{}.crt";
  private static final String CERT_ALIAS = "cert";
  public static final String JKS = "JKS";
  private static final String X_509 = "X.509";
  public static final String RSA = "RSA";
  public static final String JKS_DEFAULT_PASSWORD = "changeit";
  public static final String KEY_FILE_NAME = "key.pkcs8";

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
    return (X509Certificate)callWithCloseable(bytes::openStream, s->certFactory.generateCertificate(s));
  }

  public static final X509Certificate readCertificateFromPem(final String pem){
    final CertificateFactory certFactory = call(()->CertificateFactory.getInstance(X_509));
    return (X509Certificate)callWithCloseable(
      ()->new ByteArrayInputStream(pem.getBytes(StandardCharsets.US_ASCII)),
      s->certFactory.generateCertificate(s)
    );
  }

  public static final IList<X509Certificate> readCertificateChainFromPem(final String pem){
    final CertificateFactory certFactory = call(()->CertificateFactory.getInstance(X_509));
    return callWithCloseable(
      ()->new ByteArrayInputStream(pem.getBytes(StandardCharsets.US_ASCII)),
      s->{
        final IList.Builder<X509Certificate> b = iCollections().listBuilder();
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

  public static final IList<X509Certificate> readCertificateChain(final Path folder){
    final IList.Builder<X509Certificate> chain = iCollections().listBuilder();
    int i = 0;
    Path certFile = certFile(folder, i);
    while(Files.exists(certFile)){
      chain.add(readCertificate(ByteUtils.newFileBytes(certFile)));
      certFile = certFile(folder, ++i);
    }
    return chain.build();
  }

  private static Path certFile(final Path folder, final int i) {
    return folder.resolve(format(CERT_FILE_PATTERN,withLeadingZeros(i+1,2)));
  }


  public static final KeyStore readKeyStore(final ThrowingSupplier<InputStream> streamSupplier){
    final KeyStore ks = call(()->KeyStore.getInstance(JKS));
    doWithCloseable(streamSupplier, s->ks.load(s, JKS_DEFAULT_PASSWORD.toCharArray()));
    return ks;
  }

  public static final Bytes createJKSKeyStore(
    final RsaKeyPair privKey, final IList<X509Certificate> certChain
  ){
    final X509Certificate cert = certChain.get(0);
    verifyEqual(privKey.getPublic(), cert.getPublicKey());
    final KeyStore keystore = call(()->KeyStore.getInstance(JKS));
    run(()->keystore.load(null, null));
    run(()->keystore.setKeyEntry(
      CERT_ALIAS, privKey.getPrivate(),
      JKS_DEFAULT_PASSWORD.toCharArray(),
      certChain.toArray(new Certificate[certChain.size()])
    ));
    Bytes result;
    try(BytesBuilder builder = ByteUtils.newBytesBuilder()){
      run(()->keystore.store(builder, JKS_DEFAULT_PASSWORD.toCharArray()));
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
    return call(()->{
      final KeyStore ks = readKeyStore(keyStore::openStream);
      final KeyManagerFactory kmf = KeyManagerFactory.getInstance(SUN_X509);
      kmf.init(ks, SecUtils.JKS_DEFAULT_PASSWORD.toCharArray());
      final KeyManager[] keyManagers = kmf.getKeyManagers();
      final SSLContext sslContext = call(()->SSLContext.getInstance(TLSV12));
      sslContext.init(keyManagers, null, null);
      return sslContext.getServerSocketFactory();
    });
  }

  public static final SSLSocketFactory createSocketFactory(final X509Certificate cert){
    return call(()->{
      final KeyStore ks = KeyStore.getInstance(JKS);
      ks.load(null, null);
      ks.setCertificateEntry("cert", cert);
      final TrustManagerFactory tmf = TrustManagerFactory.getInstance(PKIX);
      tmf.init(ks);
      final SSLContext sslContext = call(()->SSLContext.getInstance(TLSV12));
      sslContext.init(null, tmf.getTrustManagers() , null);
      return sslContext.getSocketFactory();
    });
  }

}
