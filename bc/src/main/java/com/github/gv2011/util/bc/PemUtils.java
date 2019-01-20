package com.github.gv2011.util.bc;

/*-
 * #%L
 * util-bc
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
import static com.github.gv2011.util.Nothing.nothing;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.sec.SecUtils.readCertificateChainFromPem;

import java.io.StringReader;
import java.io.StringWriter;

import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.sec.RsaKeyPair;
import com.github.gv2011.util.sec.SecUtils;

public class PemUtils {

  private PemUtils(){staticClass();}

  public static RsaKeyPair decodePemRsaKeyPair(final String pemEncodedKeyPair) {
    return callWithCloseable(
      ()->new PEMParser(new StringReader(pemEncodedKeyPair)),
      p->(RsaKeyPair)RsaKeyPair.create(
        new JcaPEMKeyConverter().getKeyPair((PEMKeyPair) p.readObject())
      )
    );
  }

  public static String encodeAsPem(final RsaKeyPair rsaKeyPair) {
    final StringWriter result = new StringWriter();
    callWithCloseable(
      ()->new JcaPEMWriter(result),
      pw->{
        pw.writeObject(rsaKeyPair.asKeyPair());
        return nothing();
      }
    );
    return result.toString();
  }

  public static Bytes createJKSKeyStore(final String keyPem, final String certChainPem){
    return SecUtils.createJKSKeyStoreBytes(
      decodePemRsaKeyPair(keyPem),
      readCertificateChainFromPem(certChainPem)
    );
  }

}
