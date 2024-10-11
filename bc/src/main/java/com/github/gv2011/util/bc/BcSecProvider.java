package com.github.gv2011.util.bc;

import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.bytes.ByteUtils.parseBase64;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.notYetImplemented;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.OpenSSHPublicKeyUtil;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.sec.CertificateBuilder;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.OpenSshRsaPublicKey;
import com.github.gv2011.util.sec.SecProvider;
import com.github.gv2011.util.sec.SimpleKeyStore;

public final class BcSecProvider implements SecProvider{

  private static final String SSH_RSA = "ssh-rsa";
  private static final Pattern ID_RSA_PUB_PATTERN = Pattern.compile("(?<algorithm>\\S+)\\s+(?<keyData>\\S+)(\\s+(?<comment>.*))?");


  @Override
  public CertificateBuilder createCertificateBuilder() {
    return new BcCertificateBuilder();
  }

  @Override
  public SimpleKeyStore createSimpleKeyStore(final Domain domain) {
    return notYetImplemented();
  }

  @Override
  public SimpleKeyStore loadSimpleKeyStore(final TypedBytes bytes) {
    return notYetImplemented();
  }

  @Override
  public OpenSshRsaPublicKey parseOpenSshRsaPublicKey(final String idRsaPub) {
    final RSAKeyParameters rsaPublic;
    final String comment;
    {
      final Matcher matcher = ID_RSA_PUB_PATTERN.matcher(idRsaPub.trim());
      verify(matcher.matches(), ()->format("Invalid OpenSSH RSA public key format:\n{}", idRsaPub));

      verifyEqual(notNull(matcher.group("algorithm")), SSH_RSA, (e,a)->format("Algorithm {} is not supported.", a));

      comment = Opt.ofNullable(matcher.group("comment")).orElse("");

      rsaPublic = (RSAKeyParameters) OpenSSHPublicKeyUtil.parsePublicKey(
        parseBase64(notNull(matcher.group("keyData"))).toByteArray()
      );
      verify(!rsaPublic.isPrivate());

    }
    return BeanUtils.beanBuilder(OpenSshRsaPublicKey.class)
      .set(OpenSshRsaPublicKey::modulus).to(rsaPublic.getModulus())
      .set(OpenSshRsaPublicKey::publicExponent).to(rsaPublic.getExponent())
      .set(OpenSshRsaPublicKey::comment).to(comment)
      .build()
    ;
  }

}
