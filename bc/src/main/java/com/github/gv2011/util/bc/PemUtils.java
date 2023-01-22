package com.github.gv2011.util.bc;

import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.icol.ICollections.nothing;
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
