package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.run;
import static java.lang.Math.min;

import java.io.InputStream;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class TruncatedStream extends InputStream{

  private final InputStream in;
  private long remaining;

  public TruncatedStream(final InputStream in, final long offset, final long size) {
    this.in = in;
    run(()->in.skip(offset));
    remaining = size;
  }

  @Override
  public int read(){
    int result;
    if(remaining==0) result = -1;
    else{
      result = call(in::read);
      if(result==-1) throw new IllegalStateException("Premature end of stream.");
      remaining--;
    }
    return result;
  }

  @Override
  public int read(final byte[] b, final int off, final int len){
    int result;
    if(len==0) result = 0;
    else if(remaining==0) result = -1;
    else{
      result = call(()->in.read(b, off, (int)min(remaining,len)));
      if(result==-1) throw new IllegalStateException("Premature end of stream.");
      remaining-=result;
    }
    return result;
  }

  @Override
  public void close(){
    run(in::close);
  }


}
