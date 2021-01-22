package com.github.gv2011.util.bc;

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.icol.ICollections.emptySortedSet;
import static com.github.gv2011.util.icol.ICollections.listOf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.security.auth.x500.X500Principal;

import com.github.gv2011.util.StringUtils;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.DataType;
import com.github.gv2011.util.bytes.DataTypes;
import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.sec.CertificateBuilder;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.RsaKeyPair;
import com.github.gv2011.util.sec.SecProvider;
import com.github.gv2011.util.sec.SecUtils;
import com.github.gv2011.util.sec.SimpleKeyStore;

final class SimpleKeyStoreImp implements SimpleKeyStore {
  
  private static final DataType DATA_TYPE = DataTypes.APPLICATION_OCTET_STREAM;
  private static final String CERT_ALIAS = "cert";


  private final Domain domain;
  private @Nullable byte[] bytes;

  SimpleKeyStoreImp(Domain domain) {
    this.domain = domain;
    RsaKeyPair privateKey = RsaKeyPair.create();
    
    CertificateBuilder cb = SecProvider.instance().createCertificateBuilder();
    cb.setDomains(pair(domain, emptySortedSet()));
    final X509Certificate cert = cb.build(RsaKeyPair.create());
    
    IList<X509Certificate> certificatChain = listOf(cert);
    final KeyStore keystore = SecUtils.createJKSKeyStore(privateKey, certificatChain);
    try{
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      call(()->keystore.store(bos, SecUtils.JKS_DEFAULT_PASSWORD.toCharArray()));
      bytes = bos.toByteArray();
    }
    finally{
      SecUtils.asDestroyable(keystore).destroy();
    }
  }

  SimpleKeyStoreImp(TypedBytes bytes) {
    verifyEqual(bytes.dataType(), DATA_TYPE);
    this.bytes = bytes.content().toByteArray();
    this.domain = Domain.parse(
      StringUtils.removePrefix(
        (
          call(()->(X509Certificate)asKeyStore().getCertificate(CERT_ALIAS))
          .getSubjectX500Principal()
          .getName(X500Principal.CANONICAL)
        ),
        "CN="
      )
    );
  }

  @Override
  public Domain domain() {
    return domain;
  }

  @Override
  public synchronized KeyStore asKeyStore() {
    verify(!isDestroyed());
    return SecUtils.readKeyStore(()->new ByteArrayInputStream(bytes));
  }

  @Override
  public synchronized TypedBytes asBytes() {
    verify(!isDestroyed());
    return ByteUtils.newBytes(bytes).typed(DATA_TYPE);
  }

  @Override
  public synchronized void destroy() {
    Arrays.fill(bytes, (byte)0);
    bytes = null;
  }

  @Override
  public synchronized boolean isDestroyed() {
    return bytes==null;
  }

}
