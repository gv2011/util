package com.github.gv2011.util.bc;

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.bytes.ByteUtils.parseBase64;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.notYetImplemented;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.OutputStreamWriter;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.util.OpenSSHPrivateKeyUtil;
import org.bouncycastle.crypto.util.OpenSSHPublicKeyUtil;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.Verify;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.bytes.BytesBuilder;
import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.sec.CertificateBuilder;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.OpenSshPublicKey;
import com.github.gv2011.util.sec.RsaKeyPair;
import com.github.gv2011.util.sec.SecProvider;
import com.github.gv2011.util.sec.SimpleKeyStore;
import com.github.gv2011.util.sec.UnixSha512CryptHash;

public final class BcSecProvider implements SecProvider{

  private static final String SSH_RSA = "ssh-rsa";
  private static final Pattern ID_RSA_PUB_PATTERN = Pattern.compile("(?<algorithm>\\S+)\\s+(?<keyData>\\S+)(\\s+(?<comment>.*))?");


  static final Pattern ID_RSA_PRIV_PATTERN = Pattern.compile(
    "\\s*\\Q-----BEGIN OPENSSH PRIVATE KEY-----\\E\\s+(?<keyData>[\\S]+[\\s\\S]*[\\S]+)[\\s]+\\Q-----END OPENSSH PRIVATE KEY-----\\E\\s*")
  ;


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
  public Pair<Bytes,String> unpackOpenSshRsaPublicKey(final String idRsaPub){
    final Bytes keyData;
    final String comment;
    {
      final Matcher matcher = ID_RSA_PUB_PATTERN.matcher(idRsaPub.trim());
      Verify.verify(matcher.matches(), ()->format("Invalid OpenSSH RSA public key format:\n{}", idRsaPub));

      verifyEqual(notNull(matcher.group("algorithm")), SSH_RSA, (e,a)->format("Algorithm {} is not supported.", a));

      comment = Opt.ofNullable(matcher.group("comment")).orElse("");

      keyData = parseBase64(notNull(matcher.group("keyData")));
    }
    return pair(keyData, comment);
  }

  @Override
  public OpenSshPublicKey parseOpenSshRsaPublicKey(final Bytes idRsaPubUnpacked, final String comment){
    final RSAKeyParameters rsaPublic = (RSAKeyParameters) OpenSSHPublicKeyUtil.parsePublicKey(
      idRsaPubUnpacked.toByteArray()
    );
    Verify.verify(!rsaPublic.isPrivate());
    return BeanUtils.beanBuilder(OpenSshPublicKey.class)
      .set(OpenSshPublicKey::modulus).to(rsaPublic.getModulus())
      .set(OpenSshPublicKey::publicExponent).to(rsaPublic.getExponent())
      .set(OpenSshPublicKey::comment).to(comment)
      .build()
    ;
  }


  @Override
  public Bytes unpackOpenSshRsaPrivateKey(final String idRsa) {
    final Matcher matcher = ID_RSA_PRIV_PATTERN.matcher(idRsa);
    Verify.verify(matcher.matches(), ()->format("Invalid OpenSSH RSA private key format:\n{}", idRsa));
    return parseBase64(notNull(matcher.group("keyData")).replaceAll("\\s", ""));
  }

  @Override
  public RsaKeyPair parseOpenSshRsaPrivateKey(final Bytes idRsaUnpacked) {
    final RSAPrivateCrtKeyParameters rsaPrivate = (RSAPrivateCrtKeyParameters)
      OpenSSHPrivateKeyUtil.parsePrivateKeyBlob(idRsaUnpacked.toByteArray())
    ;
    Verify.verify(rsaPrivate.isPrivate());
    return RsaKeyPair.create(createFromSpec(asRSAPrivateCrtKeySpec(rsaPrivate)));
  }

  public RSAPrivateCrtKey createFromSpec(RSAPrivateCrtKeySpec spec){
    return (RSAPrivateCrtKey) call(()->KeyFactory.getInstance("RSA").generatePrivate(spec));
  }

  public RSAPrivateCrtKeySpec asRSAPrivateCrtKeySpec(RSAPrivateCrtKeyParameters bcKey){
    return new RSAPrivateCrtKeySpec(
      bcKey.getModulus(),
      bcKey.getPublicExponent(),
      bcKey.getExponent(),
      bcKey.getP(),
      bcKey.getQ(),
      bcKey.getDP(),
      bcKey.getDQ(),
      bcKey.getQInv()
    );
  }

  @Override
  public UnixSha512CryptHash unixSha512Crypt(String password) {
    return new UnixSha512CryptHashImp(password);
  }

  @Override
  public UnixSha512CryptHash unixSha512Crypt(Bytes password) {
    return new UnixSha512CryptHashImp(password);
  }

  @Override
  public Bytes convertToPem(Bytes x509CertificateDer) {
    final BytesBuilder bb = ByteUtils.newBytesBuilder();
    callWithCloseable(()->new JcaPEMWriter(new OutputStreamWriter(bb, UTF_8)), pemWriter->{
      pemWriter.writeObject(new X509CertificateHolder(x509CertificateDer.toByteArray()));
    });
    return bb.build();
  }

}
