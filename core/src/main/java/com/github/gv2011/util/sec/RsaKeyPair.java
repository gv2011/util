package com.github.gv2011.util.sec;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
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




import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.sec.SecUtils.RSA;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

import com.github.gv2011.util.Equal;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;

public final class RsaKeyPair implements Destroyable{

  public static final RsaKeyPair create(final KeyPair keyPair){
    final RSAPrivateCrtKey priv = (RSAPrivateCrtKey)keyPair.getPrivate();
    final RSAPublicKey pub = (RSAPublicKey)keyPair.getPublic();
    check(pub, priv);
    return create(priv);
  }

  public static final RsaKeyPair create(final RSAPrivateCrtKey priv){
    return new RsaKeyPair(priv);
  }

  public static final RsaKeyPair create(){
    final KeyPairGenerator keyGen = call(()->KeyPairGenerator.getInstance(RSA));
    keyGen.initialize(4096);
    return RsaKeyPair.create(keyGen.generateKeyPair());
  }

  public static final RsaKeyPair parse(final Bytes bytes){
    final PKCS8EncodedKeySpec spec =  new PKCS8EncodedKeySpec(bytes.toByteArray());
    return RsaKeyPair.create((RSAPrivateCrtKey)call(()->KeyFactory.getInstance(RSA).generatePrivate(spec)));
  }

  public static final void check(final RSAPublicKey pub, final RSAPrivateCrtKey priv){
    verifyEqual(pub.getModulus(), priv.getModulus());
    verifyEqual(pub.getPublicExponent(), priv.getPublicExponent());
  }

  private final RSAPrivateCrtKey priv;

  private RsaKeyPair(final RSAPrivateCrtKey priv) {
    this.priv = priv;
  }

  public final String getKeyType(){
    return RSA;
  }

  public RSAPrivateCrtKey getPrivate(){
    verify(!isDestroyed());
    return priv;
  }

  public RSAPublicKey getPublic(){
    verify(!isDestroyed());
    return SecUtils.createRsaPublicKey(priv.getModulus(), priv.getPublicExponent());
  }

  @Override
  public void destroy() throws DestroyFailedException {
    priv.destroy();
  }

  @Override
  public boolean isDestroyed() {
    return priv.isDestroyed();
  }

  @Override
  public int hashCode() {
    return RsaKeyPair.class.hashCode() * 31 + getPublic().hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    return Equal.equal(this, obj, RsaKeyPair.class,
      o->{
        final RSAPublicKey thisPub = getPublic();
        final RSAPublicKey otherPub = o.getPublic();
        return thisPub.getModulus().equals(otherPub.getModulus())
            && thisPub.getPublicExponent().equals(otherPub.getPublicExponent());
      }
    );
  }

  public Bytes encode(){
    return ByteUtils.newBytes(priv.getEncoded());
  }

  public KeyPair asKeyPair(){
    return new KeyPair(getPublic(), priv);
  }

}
