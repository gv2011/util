package com.github.gv2011.testutil;

import static com.github.gv2011.util.Verify.verifyEqual;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;

import com.github.gv2011.util.sec.RsaKeyPair;


public class Rsa {
  /**
   * Find a factor of n by following the algorithm outlined in Handbook of Applied Cryptography, section
   * 8.2.2(i). See http://cacr.uwaterloo.ca/hac/about/chap8.pdf.
   * @throws InvalidKeySpecException
   * @throws NoSuchAlgorithmException
   *
   */

  public static void main(final String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException{
    final RsaKeyPair rsa = RsaKeyPair.create();
    final RSAPrivateCrtKey crt = createCrtKey(rsa.getPublic(), rsa.getPrivate());

    verifyEqual(rsa.getPrivate().getModulus(), rsa.getPublic().getModulus());

    verifyEqual(crt.getPrimeExponentP(), rsa.getPrivate().getPrimeExponentQ());
//    verifyEqual(crt.getPrimeExponentQ(), rsa.getPrivate().getPrimeExponentQ());
  }

  private static BigInteger findFactor(final BigInteger e, final BigInteger d, final BigInteger n) {
      final BigInteger edMinus1 = e.multiply(d).subtract(BigInteger.ONE);
      final int s = edMinus1.getLowestSetBit();
      final BigInteger t = edMinus1.shiftRight(s);

      for (int aInt = 2; true; aInt++) {
          BigInteger aPow = BigInteger.valueOf(aInt).modPow(t, n);
          for (int i = 1; i <= s; i++) {
              if (aPow.equals(BigInteger.ONE)) {
                  break;
              }
              if (aPow.equals(n.subtract(BigInteger.ONE))) {
                  break;
              }
              final BigInteger aPowSquared = aPow.multiply(aPow).mod(n);
              if (aPowSquared.equals(BigInteger.ONE)) {
                  return aPow.subtract(BigInteger.ONE).gcd(n);
              }
              aPow = aPowSquared;
          }
      }

  }

  public static RSAPrivateCrtKey createCrtKey(final RSAPublicKey rsaPub, final RSAPrivateKey rsaPriv)
      throws NoSuchAlgorithmException, InvalidKeySpecException {

      final BigInteger e = rsaPub.getPublicExponent();
      final BigInteger d = rsaPriv.getPrivateExponent();
      final BigInteger n = rsaPub.getModulus();
      BigInteger p = findFactor(e, d, n);
      BigInteger q = n.divide(p);
      if (p.compareTo(q) > 1) {
          final BigInteger t = p;
          p = q;
          q = t;
      }
      final BigInteger exp1 = d.mod(p.subtract(BigInteger.ONE));
      final BigInteger exp2 = d.mod(q.subtract(BigInteger.ONE));
      final BigInteger coeff = q.modInverse(p);
      final RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(n, e, d, p, q, exp1, exp2, coeff);
      final KeyFactory kf = KeyFactory.getInstance("RSA");
      return (RSAPrivateCrtKey) kf.generatePrivate(keySpec);

  }
}
