package com.github.gv2011.util.bc;

import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.listOf;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStrictStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.sec.CertificateBuilder;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.RsaKeyPair;
import com.github.gv2011.util.time.Clock;
import com.github.gv2011.util.time.TimeUtils;

public final class BcCertificateBuilder implements CertificateBuilder{

  private static final Instant REMOTE_FUTURE = Instant.parse("3000-01-01T00:00:00Z");
  private static final Instant REMOTE_PAST = Instant.parse("2000-01-01T00:00:00Z");
  private @Nullable Domain domain;
  private @Nullable LdapName subject;
  private @Nullable RSAPublicKey subjectPublicKey;
  private @Nullable Instant notBefore;
  private @Nullable Instant notAfter;
  private @Nullable LdapName issuer;
  private boolean ca;

  @Override
  public BcCertificateBuilder setSubject(final LdapName subject) {
    this.subject = subject;
    return this;
  }

  @Override
  public BcCertificateBuilder setCa(final boolean ca) {
    this.ca = ca;
    return this;
  }

  @Override
  public BcCertificateBuilder setDomains(final Pair<Domain, ISortedSet<Domain>> domains) {
    verify(subject==null);
    domain = domains.getKey();
    subject = call(()->new LdapName(format("CN={}",domains.getKey())));
    return this;
  }

  @Override
  public BcCertificateBuilder setSubjectPublicKey(final RSAPublicKey subjectPublicKey) {
    this.subjectPublicKey = subjectPublicKey;
    return this;
  }

  @Override
  public BcCertificateBuilder setNotBefore(final Instant notBefore) {
    this.notBefore = notBefore;
    return this;
  }

  @Override
  public BcCertificateBuilder setNotAfter(final Instant notAfter) {
    this.notAfter = notAfter;
    return this;
  }

  @Override
  public BcCertificateBuilder setIssuer(final LdapName issuer) {
    this.issuer = issuer;
    return this;
  }

  @Override
  public BcCertificateBuilder setIssuer(X500Principal x500Principal) {
    return setIssuer(convert(x500Principal));
  }

  @Override
  public X509Certificate build(final RsaKeyPair keyPair){
    if(ca) verify(this.subjectPublicKey==null);

    final LdapName subject = Optional.ofNullable(this.subject).orElseGet(()->asName(keyPair.getPublic()));
    final RSAPublicKey subjectPublicKey = Optional.ofNullable(this.subjectPublicKey).orElse(keyPair.getPublic());

    final Instant notBefore;
    final Instant notAfter;
    if(ca || domain==null){
      notBefore = Optional.ofNullable(this.notBefore).orElseGet(()->REMOTE_PAST);
      notAfter = Optional.ofNullable(this.notAfter).orElse(REMOTE_FUTURE);
    }
    else{
      notBefore = Optional.ofNullable(this.notBefore).orElseGet(()->
        LocalDate.ofInstant(Clock.get().instant().minus(1, ChronoUnit.MINUTES), TimeUtils.UTC)
        .atStartOfDay().toInstant(ZoneOffset.UTC)
      );
      notAfter = Optional.ofNullable(this.notAfter).orElseGet(()->
        LocalDate.ofInstant(notBefore, TimeUtils.UTC).plusYears(1).plusWeeks(2)
        .atStartOfDay().toInstant(ZoneOffset.UTC)
      );
    }
    verify(notBefore.isBefore(notAfter));

    BigInteger serial = BigInteger.valueOf(new SecureRandom().nextLong()).abs();

    final LdapName issuer;
    if(ca){
      verify(this.issuer==null ? true : this.issuer.equals(subject));
      issuer = subject;
    }
    else{
      issuer = Optional.ofNullable(this.issuer).orElse(subject);
    }

    final JcaX509v3CertificateBuilder v3CertGen = new JcaX509v3CertificateBuilder(
      convert(notNull(issuer)),
      serial,
      Date.from(notNull(notBefore)), Date.from(notNull(notAfter)),
      convert(notNull(subject)),
      convert(notNull(subjectPublicKey))
    );

    if(ca) call(()->{
      v3CertGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
      v3CertGen.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign));
    });
    else if(domain!=null) call(()->{
      // -----------------------------------------------------------------------------
      // * Subject Alternative Name (SAN)
      // Modern browsers (Chrome, Firefox, Edge) require the hostname to appear in
      // the SAN extension, not just in the CN (Common Name). Without this extension,
      // the certificate will be rejected as invalid, even if CN matches the domain.
      // Here we add a single DNS SAN entry matching the given domain.
      // -----------------------------------------------------------------------------
      GeneralName san = new GeneralName(GeneralName.dNSName, domain.toAscii());
      v3CertGen.addExtension(
        Extension.subjectAlternativeName,
        false,                        // not critical: browsers ignore if unknown
        new GeneralNames(san)         // wrap single SAN into a GeneralNames set
      );

      // -----------------------------------------------------------------------------
      // * Basic Constraints
      // Indicates whether the certificate is a CA (can sign other certificates).
      // For a leaf (end-entity) certificate, this must be "false" (non-CA).
      // Browsers and TLS stacks will reject a leaf cert marked as CA=true.
      // The second argument "true" here marks the extension itself as *critical*,
      // which is common practice for BasicConstraints.
      // -----------------------------------------------------------------------------
      v3CertGen.addExtension(
        Extension.basicConstraints,
        true,                          // critical: clients must understand it
        new BasicConstraints(false)    // false = this is NOT a CA certificate
      );

      // -----------------------------------------------------------------------------
      // * Key Usage
      // Defines what the certificate’s key can be used for.
      // * digitalSignature → needed for TLS handshake authentication
      // * keyEncipherment  → allows key exchange during TLS setup
      // These two flags are required for typical HTTPS server certificates.
      // Marked critical so that clients must respect intended usage.
      // -----------------------------------------------------------------------------
      v3CertGen.addExtension(
        Extension.keyUsage,
        true,  // critical
        new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment)
      );
    });

    final ContentSigner contentSigner = createContentSigner(keyPair.getPrivate());
    final X509CertificateHolder certHolder = v3CertGen.build(contentSigner);
    final X509Certificate certificate = call(()->
      new JcaX509CertificateConverter()
      .getCertificate(certHolder)
    );
    call(()->certificate.verify(keyPair.getPublic()));
    return certificate;
  }

  private LdapName asName(final RSAPublicKey publicKey) {
    return new LdapName(listOf(call(()->new Rdn("CN", ByteUtils.newBytes(publicKey.getEncoded()).hash().content().toHex()))));
  }

  private SubjectPublicKeyInfo convert(final RSAPublicKey publicKey) {
    return SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
  }

  private ContentSigner createContentSigner(final RSAPrivateCrtKey privKey) {
    return call(()->new JcaContentSignerBuilder("SHA256withRSA").build(privKey));
  }

  private X500Name convert(final LdapName name){
    final X500Name result = new X500Name(BCStrictStyle.INSTANCE, name.toString());
    verifyEqual(name.toString(), result.toString());
    return result;
  }

  private LdapName convert(final X500Principal name){
    LdapName result = call(()->new LdapName(name.getName(X500Principal.RFC2253)));
    verifyEqual(name.toString(), result.toString());
    return result;
  }

  @SuppressWarnings("unused")
  private RSAPrivateCrtKeyParameters convert(final RSAPrivateCrtKey privKey) {
    final BigInteger  modulus = privKey.getModulus();
    final BigInteger  publicExponent = privKey.getPublicExponent();
    final BigInteger  privateExponent = privKey.getPrivateExponent();
    final BigInteger  p = privKey.getPrimeP();
    final BigInteger  q = privKey.getPrimeQ();
    final BigInteger  dP = privKey.getPrimeExponentP();
    final BigInteger  dQ = privKey.getPrimeExponentQ();
    final BigInteger  qInv = privKey.getCrtCoefficient();
    return new RSAPrivateCrtKeyParameters(modulus, publicExponent, privateExponent, p, q, dP, dQ, qInv);
  }

}
