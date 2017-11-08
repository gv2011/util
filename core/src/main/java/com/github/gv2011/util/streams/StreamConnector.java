package com.github.gv2011.util.streams;

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



import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.run;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.bytes.BytesBuilder;

public class StreamConnector {

  public static final StreamConnector create(){
    return create(8192);
  }

  public static final StreamConnector create(final long limit){
    return new StreamConnector(limit);
  }

  private final OutputStream out;
  private final InputStream in;

  private final Object lock = new Object();
  private @Nullable BytesBuilder buffer;
  private boolean outClosed = false;
  private boolean inClosed = false;
  private final long limit;

  private StreamConnector(final long limit){
    out = new Out();
    in = new In();
    this.limit = limit;
    buffer = ByteUtils.newBytesBuilder();
  }

  public OutputStream outputStream(){
    return out;
  }

  public InputStream inputStream(){
    return in;
  }

  private class Out extends OutputStream {

    @Override
    public void write(final int b) throws IOException {
      write(new byte[]{(byte) b},0,1);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
      verify(len<=limit);
      synchronized(lock){
        if(outClosed) throw new IOException("Closed.");
        if(!inClosed) {
          while(!canWrite(len)) run(lock::wait);
          if(!inClosed)buffer.write(b, off, len);
        }
        lock.notifyAll();
      }
    }

    private boolean canWrite(final int len) {
      return inClosed ? true : buffer.isEmpty() ? true : buffer.size()+len <= limit;
    }

    @Override
    public void close() throws IOException {
      synchronized(lock){outClosed = true;}
    }
  }


  private class In extends InputStream {

    @Override
    public int read() throws IOException {
      final byte[] buffer = new byte[1];
      final int count = read(buffer, 0, 1);
      return count==-1 ? -1 : buffer[0];
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
      int result;
      synchronized(lock){
        if(inClosed) throw new IOException("Closed.");
        if(len==0) result = 0;
        else{
          while(buffer.isEmpty() && !outClosed) run(lock::wait);
          if(!buffer.isEmpty()){
            final int count = (int) Math.min(len, buffer.size());
            final Bytes bytes = buffer.build();
            final int count2 = bytes.write(b, off, count);
            verifyEqual(count2, count);
            buffer = ByteUtils.newBytesBuilder().append(bytes.subList(count, bytes.size()));
            result = count;
          }else{
            verify(outClosed);
            result = -1;
          }
        }
        lock.notifyAll();
      }
      return result;
    }

    @Override
    public int available() throws IOException {
      synchronized(lock){
        if(inClosed) throw new IOException("Closed.");
        return (int) buffer.size();
      }
    }

    @Override
    public void close() throws IOException {
      synchronized(lock){
        inClosed = true;
        buffer = null;
        lock.notifyAll();
      }
    }

  }

}
