package com.github.gv2011.util.sec;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.naming.ldap.LdapName;

public class CertificateBuilderManualTest {

  public static void main(final String[] args) throws Exception{
    final RsaKeyPair keyPair = RsaKeyPair.create();
    final X509Certificate cert = SecProvider.instance().createCertificateBuilder()
      .setSubject(new LdapName("CN=Vinz"))
      .build(keyPair)
    ;
    final KeyStore ks = KeyStore.getInstance(SecUtils.PKCS12);
    ks.load(null, null);
    ks.setKeyEntry("default", keyPair.getPrivate(), "".toCharArray(), new Certificate[]{cert});
    try(OutputStream out =
      Files.newOutputStream(Paths.get("cert.p12"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    ){
      ks.store(out, "".toCharArray());
    }
  }

}
