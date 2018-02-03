package com.github.gv2011.util.bytes;

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
import static java.lang.Math.min;

import java.io.InputStream;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class TruncatedStream extends InputStream{

  private final InputStream in;
  private long remaining;

  public TruncatedStream(final InputStream in, final long offset, final long size) {
    this.in = in;
    call(()->in.skip(offset));
    remaining = size;
  }

  @Override
  public int read(){
    int result;
    if(remaining==0) result = -1;
    else{
      result = call(()->in.read());
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
    call(in::close);
  }


}
