package com.github.gv2011.util;

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




import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.IntConsumer;

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

  public static long countAndClose(final ThrowingSupplier<InputStream> in){
    return callWithCloseable(in, s->{
      return count(s);
    });
  }

  public static long count(final InputStream s){
    final byte[] buffer = new byte[1024];
    long count = 0;
    int read = call(()->s.read(buffer));
    while(read!=-1){
      count += read;
      read = call(()->s.read(buffer));
    }
    return count;
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

  public static CloseableIntIterator asIterator(final InputStream in) {
    return new StreamIterator(in);
  }

  public static final InputStream countingStream(final InputStream in, final IntConsumer counter){
    return countingStream(in, counter, i->i);
  }

  @FunctionalInterface
  public static interface Throttler{
    /**
     * @param limit never less than 1
     * @return int between 1 and limit
     */
    int maxReadCount(int limit);
  }

  public static final InputStream countingStream(
    final InputStream in, final IntConsumer counter, final Throttler throttler
  ){
    return new InputStream(){
      @Override
      public int read() throws IOException {
        throttler.maxReadCount(1);
        final int next = in.read();
        if(next!=-1) counter.accept(1);
        return next;
      }
      @Override
      public int read(final byte[] b, final int off, final int len) throws IOException {
        final int count = in.read(b, off, throttler.maxReadCount(len));
        if(count!=-1) counter.accept(count);
        return count;
      }
      @Override
      public void close() throws IOException {
        in.close();
      }
    };
  }

}
