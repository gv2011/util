package com.github.gv2011.util.bc;

/*-
 * #%L
 * util-test
 * %%
 * Copyright (C) 2016 - 2019 Vinz (https://github.com/gv2011)
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

import static com.github.gv2011.testutils.Matchers.hasSize;
import static com.github.gv2011.testutils.Matchers.is;
import static com.github.gv2011.testutils.Matchers.notNull;
import static com.github.gv2011.util.icol.ICollections.iCollections;
import static org.junit.Assert.assertThat;

import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.junit.Test;

import com.github.gv2011.testutils.AbstractTest;
import com.github.gv2011.util.bc.PemUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.icol.IList;
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
    final IList<X509Certificate> chainIn = SecUtils.readCertificateChainFromPem(certChainPem);
    assertThat(chainIn, hasSize(2));
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
