package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.NumUtils.isOdd;
import static com.github.gv2011.util.StringUtils.removeWhitespace;
import static com.github.gv2011.util.ex.Exceptions.call;
import static java.lang.Math.min;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public class ByteUtils {

  private static final Bytes EMPTY = newBytes(new byte[0]);

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

  public static Bytes asBytes(final int i){
    final byte[] array = new byte[4];
    array[3] = (byte)(i);
    array[2] = (byte)(i>>8);
    array[1] = (byte)(i>>16);
    array[0] = (byte)(i>>24);
    return new ArrayBytes(array);
  }

  public static Bytes asUtf8(final String text){
    return new ArrayBytes(text.getBytes(UTF_8));
  }

  public static Hash256 hash(final String text){
    return call(()->
      new Hash256Imp(MessageDigest.getInstance(Hash256Imp.ALGORITHM).digest(text.getBytes(UTF_8)))
    );
  }

  public static CloseableBytes newRandomBytes(final long size){
    final byte[] bytes = new byte[(int)min(1024,size)];
    final Random random = new SecureRandom();
    long remaining = size;
    try(BytesBuilder builder = newBytesBuilder()){
      while(remaining>0){
        random.nextBytes(bytes);
        final int len = (int)min(bytes.length, remaining);
        builder.write(bytes,0,len);
        remaining-=len;
      }
      return builder.build();
    }
  }

  public static CloseableBytes fromStream(final InputStream in){
    final byte[] bytes = new byte[1024];
    int count = call(()->in.read(bytes));
    try(BytesBuilder builder = newBytesBuilder()){
      while(count!=-1){
        builder.write(bytes,0,count);
        count = call(()->in.read(bytes));
      }
      return builder.build();
    }
  }

  public static FileBytes newFileBytes(final Path file) {
    return new FileBytes(file);
  }

  public static BytesBuilder newBytesBuilder() {
    return new BytesBuilder();
  }

  public static Hash256 parseHash(final String hexString){
    return new Hash256Imp(hexToByteArray(hexString));
  }

  public static Bytes emptyBytes() {
    return EMPTY;
  }


}
