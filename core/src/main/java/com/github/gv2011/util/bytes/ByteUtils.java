package com.github.gv2011.util.bytes;

/*-
 * %---license-start---
 * The MIT License (MIT)
 * %
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %
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
 * %---license-end---
 */



import static com.github.gv2011.util.NumUtils.isOdd;
import static com.github.gv2011.util.StringUtils.removeWhitespace;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static java.lang.Math.min;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

import com.github.gv2011.util.ex.ThrowingSupplier;

public class ByteUtils {

  private ByteUtils(){staticClass();}

  private static final Bytes EMPTY = newBytes(new byte[0]);

  public static Bytes newBytes(final byte... bytes){
    return newBytes(bytes, 0, bytes.length);
  }

  public static Bytes newBytes(final byte[] bytes, final int from, final int to) {
    final byte[] copy = Arrays.copyOfRange(bytes, from, to);
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

  public static Bytes newRandomBytes(final long size){
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

  public static Bytes fromStream(final InputStream in){
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

  public static Bytes copyFromStream(final ThrowingSupplier<InputStream> in){
    return callWithCloseable(
      in::get,
      s->{
        final byte[] buffer = new byte[1024];
        int count = s.read(buffer);
        final BytesBuilder builder = newBytesBuilder();
        while(count!=-1){
          builder.write(buffer,0,count);
          count = s.read(buffer);
        }
        return builder.build();
      }
    );
  }

  public static Bytes copyFromStream(final InputStream in, final long limit) {
    verify(limit>=0);
    if(limit==0) return ByteUtils.EMPTY;
    else{
      final BytesBuilder builder = newBytesBuilder();
      long remaining = limit;
      final byte[] buffer = new byte[(int)Math.min(1024, remaining)];
      int count = call(()->in.read(buffer));
      while(remaining>0){
        if(count==-1) remaining=-1;
        else{
          builder.write(buffer,0,count);
          remaining-=count;
          final int readLimit = (int) Math.min(buffer.length, remaining);
          count = call(()->in.read(buffer,0,readLimit));
        }
      }
      return builder.build();
    }
  }



  public static Optional<Bytes> tryRead(final Path file) {
    return Files.exists(file) ? Optional.of(newFileBytes(file)) : Optional.empty();
  }

  public static Bytes newFileBytes(final Path file) {
    return new FileBytes(file);
  }

  public static BytesBuilder newBytesBuilder() {
    return new BytesBuilder();
  }

  public static BytesBuilder newBytesBuilder(final int initialBufferSize) {
    return new BytesBuilder(initialBufferSize);
  }

  public static Hash256 parseHash(final String hexString){
    return new Hash256Imp(hexToByteArray(hexString));
  }

  public static Bytes emptyBytes() {
    return EMPTY;
  }

  public static Bytes fromBigInteger(final BigInteger i) {
    return new ArrayBytes(i.toByteArray());
  }


}
