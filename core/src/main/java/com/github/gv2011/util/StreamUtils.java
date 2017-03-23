package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.io.InputStream;

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

}
