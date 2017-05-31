package com.github.gv2011.util;

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
