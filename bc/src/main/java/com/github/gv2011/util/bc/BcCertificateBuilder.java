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
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.listOf;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStrictStyle;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.sec.CertificateBuilder;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.RsaKeyPair;
import com.github.gv2011.util.sec.SecUtils;

public final class BcCertificateBuilder implements CertificateBuilder{

  public static final class Supplier implements CertificateBuilderSupplier{
    @Override
    public CertificateBuilder get() {
      return new BcCertificateBuilder();
    }
  }


  private static final Instant REMOTE_FUTURE = Instant.parse("3000-01-01T00:00:00Z");
  private static final Instant REMOTE_PAST = Instant.parse("2000-01-01T00:00:00Z");
  private @Nullable LdapName subject;
  private @Nullable RSAPublicKey subjectPublicKey;
  private @Nullable Instant notBefore;
  private @Nullable Instant notAfter;
  private @Nullable LdapName issuer;

  public BcCertificateBuilder(){
  }

  @Override
  public BcCertificateBuilder setSubject(final LdapName subject) {
    this.subject = subject;
    return this;
  }

  @Override
  public BcCertificateBuilder setDomains(final Pair<Domain, ISortedSet<Domain>> domains) {
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
  public X509Certificate build(final RsaKeyPair keyPair){
    final LdapName subject = Optional.ofNullable(this.subject).orElseGet(()->asName(keyPair.getPublic()));
    final RSAPublicKey subjectPublicKey = Optional.ofNullable(this.subjectPublicKey).orElse(keyPair.getPublic());
    final Instant notBefore = Optional.ofNullable(this.notBefore).orElse(REMOTE_PAST);
    final Instant notAfter = Optional.ofNullable(this.notAfter).orElse(REMOTE_FUTURE);
    final LdapName issuer = Optional.ofNullable(this.issuer).orElse(subject);

    final X509v1CertificateBuilder v1CertGen = new X509v1CertificateBuilder(
      convert(notNull(issuer)),
      BigInteger.ONE,
      Date.from(notNull(notBefore)), Date.from(notNull(notAfter)),
      convert(notNull(subject)),
      convert(notNull(subjectPublicKey))
    );
    final ContentSigner contentSigner = createContentSigner(keyPair.getPrivate());
    final X509CertificateHolder certHolder = v1CertGen.build(contentSigner);
    return SecUtils.readCertificate(ByteUtils.newBytes(call(certHolder::getEncoded)));
  }

  private LdapName asName(final RSAPublicKey publicKey) {
    return new LdapName(listOf(call(()->new Rdn("CN", ByteUtils.newBytes(publicKey.getEncoded()).toHex()))));
  }

  private SubjectPublicKeyInfo convert(final RSAPublicKey publicKey) {
    return SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
  }

  private ContentSigner createContentSigner(final RSAPrivateCrtKey privKey) {
    final AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA1withRSA");
    final AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);

    final AsymmetricKeyParameter lwPrivKey = convert(privKey);
    return call(()->new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(lwPrivKey));
  }

  private X500Name convert(final LdapName name){
    final X500Name result = new X500Name(BCStrictStyle.INSTANCE, name.toString());
    verifyEqual(name.toString(), result.toString());
    return result;
  }

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
