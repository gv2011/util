package com.github.gv2011.util.bc;

import static com.github.gv2011.util.CollectionUtils.intStream;

import java.security.SecureRandom;
import java.util.Random;

import org.bouncycastle.crypto.generators.OpenBSDBCrypt;

import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.sec.UnixSha512CryptHash;
import com.github.gv2011.util.tstr.AbstractTypedString;

final class UnixSha512CryptHashImp extends AbstractTypedString<UnixSha512CryptHash> implements UnixSha512CryptHash {

  private static final String SALT_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789./";
  private static final int SALT_LENGHT = 16;
  private static final Random RANDOM = new SecureRandom();

  private final String hash;


  UnixSha512CryptHashImp(String password) {
    this(ByteUtils.asUtf8(password).content());
  }

  UnixSha512CryptHashImp(Bytes password) {
    hash = OpenBSDBCrypt.generate(toChars(password), generateSalt().toByteArray(), 16);
  }


  @Override
  public UnixSha512CryptHash self() {
    return this;
  }

  @Override
  public Class<UnixSha512CryptHash> clazz() {
    return UnixSha512CryptHash.class;
  }

  @Override
  public boolean verify(String password) {
    return verify(ByteUtils.asUtf8(password).content());
  }

  @Override
  public boolean verify(Bytes password) {
    return OpenBSDBCrypt.checkPassword(hash, toChars(password));
  }

  @Override
  public String toString() {
    return hash;
  }

  private static char[] toChars(Bytes password) {
    char[] passwordChars = new char[password.size()];
    for (int i = 0; i < password.size(); i++) {
      passwordChars[i] = (char) (password.getByte(i) & 0xFF);
    }
    return passwordChars;
  }

  private static Bytes generateSalt() {
    return ByteUtils.collectBytes(
     intStream(SALT_LENGHT).unordered()
     .map(i->(int)SALT_CHARS.charAt(RANDOM.nextInt(SALT_CHARS.length())))
    );
  }

}
