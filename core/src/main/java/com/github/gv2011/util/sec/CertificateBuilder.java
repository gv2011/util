package com.github.gv2011.util.sec;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
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
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.function.Supplier;

import javax.naming.ldap.LdapName;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;

public interface CertificateBuilder{

  public static interface CertificateBuilderSupplier extends Supplier<CertificateBuilder>{}

  public static CertificateBuilder create() {
    return RecursiveServiceLoader.service(CertificateBuilderSupplier.class).get();
  }

  CertificateBuilder setSubject(final LdapName subject);

  CertificateBuilder setDomains(final Pair<Domain, ISortedSet<Domain>> domains);

  CertificateBuilder setSubjectPublicKey(final RSAPublicKey subjectPublicKey);

  CertificateBuilder setNotBefore(final Instant notBefore);

  CertificateBuilder setNotAfter(final Instant notAfter);

  CertificateBuilder setIssuer(final LdapName issuer);

  X509Certificate build(final RsaKeyPair keyPair);

}
