package com.github.gv2011.util;

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



import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.github.gv2011.util.ex.ThrowingSupplier;

public final class StreamUtils {

  private StreamUtils(){staticClass();}

  public static byte[] readBytes(final InputStream in, final int length){
    return call(()->{
      final byte[] result = new byte[length];
      int sum = in.read(result);
      if(sum==-1) throw new IllegalStateException("Premature end of stream.");
      while(sum<length){
        final int read = in.read(result, sum, length-sum);
        if(read==-1) throw new IllegalStateException("Premature end of stream.");
        sum+=read;
      }
      return result;
    });
  }



  public static String readText(final ThrowingSupplier<InputStream> in){
    return new String(readAndClose(in), UTF_8);
  }

  public static byte[] readAndClose(final ThrowingSupplier<InputStream> in){
    return callWithCloseable(in, s->{
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      final byte[] buffer = new byte[1024];
      int read = s.read(buffer);
      while(read!=-1){
        bos.write(buffer, 0, read);
        read = s.read(buffer);
      }
      return bos.toByteArray();
    });
  }

  public static long copy(final ThrowingSupplier<InputStream> in, final OutputStream out) {
    final byte[] buffer = new byte[1024];
    return callWithCloseable(in, s->{
      long size=0;
      int count = s.read(buffer);
      while(count!=-1){
        out.write(buffer, 0, count);
        size += count;
        count = s.read(buffer);
      }
      return size;
    });
  }



}
