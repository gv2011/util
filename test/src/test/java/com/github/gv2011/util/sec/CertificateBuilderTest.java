package com.github.gv2011.util.sec;

import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Matchers.arrayWithSize;
import static com.github.gv2011.testutil.Matchers.instanceOf;
import static com.github.gv2011.testutil.Matchers.is;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;

import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;

import javax.naming.ldap.LdapName;

import org.junit.Test;

import com.github.gv2011.testutil.AbstractTest;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;

public class CertificateBuilderTest extends AbstractTest{

  @Test
  public void testBuild() throws Exception {
    final RsaKeyPair keyPair = RsaKeyPair.parsePkcs8(getResourceBytes("rsaprivcrt.pkcs8"));
    final LdapName subject = call(()->new LdapName("CN=test.example.com"));
    final X509Certificate cert = SecProvider.instance().createCertificateBuilder()
      .setSubject(subject)
      .setSubjectPublicKey(keyPair.getPublic())
      .build(keyPair)
    ;
    final Bytes encoded = getResourceBytes("cert.der");
    assertThat(ByteUtils.newBytes(cert.getEncoded()), is(encoded));
    assertThat(cert, is(SecUtils.readCertificate(encoded)));
  }

  @Test
  public void exampleProperties() throws Exception {
    final CertificateFactory cf = CertificateFactory.getInstance("X509");
    final Certificate certR = callWithCloseable(
      getResource("example.der")::openStream,
      s->(Certificate)cf.generateCertificate(s)
    );
    assertThat(certR, instanceOf(X509Certificate.class));
    final X509Certificate cert = (X509Certificate) certR;
    System.out.println(cert);
  }

  @Test
  public void examplePropertiesJks() throws Exception {
    final KeyStore ks = KeyStore.getInstance("JKS");
    callWithCloseable(getResource("example.jks")::openStream, s->{ks.load(s, "changeit".toCharArray());});
    final Key keyR = ks.getKey("alias1", "changeit".toCharArray());
    assertThat(keyR, instanceOf(RSAPrivateCrtKey.class));
    final RsaKeyPair key = RsaKeyPair.create((RSAPrivateCrtKey)keyR);
    assertThat(key.encode(), is(getResourceBytes("example.rsa")));
    final Certificate[] certChain = ks.getCertificateChain("alias1");
    assertThat(certChain, arrayWithSize(1));
    assertThat(ByteUtils.newBytes(certChain[0].getEncoded()), is(getResourceBytes("example.der")));
  }

}
