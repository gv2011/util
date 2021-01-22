package com.github.gv2011.util.bc;

import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.sec.CertificateBuilder;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.SecProvider;
import com.github.gv2011.util.sec.SimpleKeyStore;

public final class BcSecProvider implements SecProvider{

  @Override
  public CertificateBuilder createCertificateBuilder() {
    return new BcCertificateBuilder();
  }

  @Override
  public SimpleKeyStore createSimpleKeyStore(Domain domain) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SimpleKeyStore loadSimpleKeyStore(TypedBytes bytes) {
    // TODO Auto-generated method stub
    return null;
  }

}
