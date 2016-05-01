package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.NumUtils.isOdd;
import static com.github.gv2011.util.StringUtils.removeWhitespace;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Arrays;

public class ByteUtils {

  public static Bytes newBytes(final byte[] bytes){
    final byte[] copy = Arrays.copyOf(bytes, bytes.length);
    return new ArrayBytes(copy);
  }

  public static Bytes parseHex(final String hex){
    return new ArrayBytes(hexToByteArray(hex));
  }

  public static byte[] hexToByteArray(final String hex){
    final String noWhitespace = removeWhitespace(hex);
    if(isOdd(noWhitespace.length())) throw new IllegalArgumentException();
    final int size = noWhitespace.length()/2;
    final byte[] b = new byte[size];
    for(int i=0; i<size; i++){
      b[i] = (byte)(
        Character.digit(noWhitespace.charAt(i*2)  , 16)<<4 |
        Character.digit(noWhitespace.charAt(i*2+1), 16)
      );
    }
    return b;
  }

  public static Bytes newBytes(final Bytes bytes){
    return new ArrayBytes(bytes.toByteArray());
  }

  public static Bytes asUtf8(final String text){
    return new ArrayBytes(text.getBytes(UTF_8));
  }

  public static Bytes newRandomBytes(final int size){
    final byte[] bytes = new byte[size];
    new SecureRandom().nextBytes(bytes);
    return new ArrayBytes(bytes);
  }

  public static FileBytes newFileBytes(final Path file) {
    final File f = file.toFile();
    return new FileBytes(f, 0, f.length());
  }

  public static Hash256 parseHash(final String hexString){
    return new Hash256Imp(hexToByteArray(hexString));
  }


}
