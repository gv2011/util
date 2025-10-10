package com.github.gv2011.util.bc;

import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Matchers.hasSize;
import static com.github.gv2011.testutil.Matchers.is;
import static com.github.gv2011.testutil.Matchers.notNull;
import static com.github.gv2011.util.icol.ICollections.iCollections;

import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Enumeration;

import org.junit.Test;

import com.github.gv2011.testutil.AbstractTest;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.sec.CertificateChain;
import com.github.gv2011.util.sec.RsaKeyPair;
import com.github.gv2011.util.sec.SecUtils;

public class PemUtilsTest extends AbstractTest{

  @Test
  public void testReadKeyPair() {
    final String pem = getResourceAsString("keypair.pem");
    final RsaKeyPair kp = PemUtils.decodePemRsaKeyPair(pem);
    assertThat(kp, notNull());
  }

  @Test
  public void testCreateJKSKeyStore() throws Exception {
    final String keyPem = getResourceAsString("key");
    final String certChainPem = getResourceAsString("chain.crt");
    final CertificateChain chainIn = SecUtils.readCertificateChainFromPem(certChainPem);
    assertThat(chainIn.certificates(), hasSize(2));
    final Bytes ks = PemUtils.createJKSKeyStore(keyPem, certChainPem);
    final KeyStore keyStore = SecUtils.readKeyStore(ks::openStream);
    final Enumeration<String> e = keyStore.aliases();
    final String alias = e.nextElement();
    assertThat(e.hasMoreElements(), is(false));

    final Key key = keyStore.getKey(alias, SecUtils.JKS_DEFAULT_PASSWORD.toCharArray());
    assertThat(key, is(PemUtils.decodePemRsaKeyPair(keyPem).getPrivate()));

    final IList<Certificate> chain = iCollections().asList(keyStore.getCertificateChain(alias));
    assertThat(chain, is(chainIn));

  }

}
