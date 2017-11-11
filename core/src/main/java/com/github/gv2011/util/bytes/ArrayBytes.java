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
import static com.github.gv2011.util.ex.Exceptions.run;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Arrays;

import net.jcip.annotations.Immutable;

@Immutable
class ArrayBytes extends AbstractBytes{

  private final byte[] bytes;

  ArrayBytes(final byte[] bytes) {
    this.bytes = bytes;
  }

  @Override
  protected Hash256 hashImp() {
    final MessageDigest md = call(()->MessageDigest.getInstance("SHA-256"));
    md.update(bytes);
    return new Hash256Imp(md);
  }


  @Override
  public byte[] toByteArray(){
    return Arrays.copyOf(bytes, bytes.length);
  }

  @Override
  public void write(final OutputStream stream){
    run(()->stream.write(bytes));
  }

  @Override
  public long longSize() {
    return bytes.length;
  }

  @Override
  public byte get(final long index) {
    if(index>Integer.MAX_VALUE) throw new IndexOutOfBoundsException();
    return bytes[(int)index];
  }

  @Override
  public byte getByte(final int index) {
    return bytes[index];
  }

  @Override
  public Bytes subList(final long fromIndex, final long toIndex) {
    final long size = longSize();
    checkIndices(fromIndex, toIndex, size);
    if(fromIndex==0 && toIndex==size) return this;
    else{
      return new ArrayBytes(Arrays.copyOfRange(bytes, (int)fromIndex, (int)toIndex));
    }
  }

  @Override
  public String utf8ToString() throws TooBigException {
    return new String(bytes, UTF_8);
  }

  @Override
  public InputStream openStream() {
    return new ByteArrayInputStream(bytes);
  }

  @Override
  public Hash256 asHash() {
    return new Hash256Imp(bytes);
  }

}
