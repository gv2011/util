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
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.wrap;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;

class FileBytes extends AbstractBytes{

  private final Path file;
  private final long offset;
  private final long size;

  FileBytes(final Path file, final long offset, final long size) {
    this.file = file;
    this.offset = offset;
    this.size = size;
  }

  protected final Path file(){
    if(closed()) throw new IllegalStateException("Closed.");
    return file;
  }

  FileBytes(final Path file) {
    this(file,0, call(()->Files.readAttributes(file, BasicFileAttributes.class).size()));
  }

  @Override
  public long longSize() {
    checkNotClosed();
    return size;
  }

  @Override
  public byte get(final long index) {
    checkNotClosed();
    if(index>=size) throw new IndexOutOfBoundsException(format("{} is greater or equal size {}.", index, size));
    try(InputStream stream = openStream()){
      stream.skip(index);
      final int b = stream.read();
      if(b<0) throw new IllegalStateException("Premature end of stream.");
      return (byte) b;
    }
    catch (final IOException e) {throw wrap(e);}
  }


  @Override
  public byte[] toByteArray() {
    final byte[] result = new byte[size()];
    try(DataInputStream din = new DataInputStream(openStream())){
      din.readFully(result);
    }catch(final IOException e){throw wrap(e);}
    return result;
  }

  @Override
  public Iterator<Byte> iterator() {
    checkNotClosed();
    return call(()->new StreamIterator(openStream()));
  }

  @Override
  public FileBytes subList(final long fromIndex, final long toIndex) {
    final long size = longSize();
    checkIndices(fromIndex, toIndex, size);
    if(fromIndex==0 && toIndex==size) return this;
    else{
      return new FileBytes(file(), fromIndex, toIndex-fromIndex);
    }
  }

  @Override
  public InputStream openStream() {
    checkNotClosed();
    return new TruncatedStream(call(()->Files.newInputStream(file())), offset, size);
  }

}
