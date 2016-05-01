package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.wrap;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

class FileBytes extends AbstractBytes{

  private final File file;
  private final long offset;
  private final long size;

  FileBytes(final File file, final long offset, final long size) {
    this.file = file;
    this.offset = offset;
    this.size = size;
  }

  @Override
  public long longSize() {
    return size;
  }

  @Override
  public byte get(final long index) {
    if(index>=size) throw new IndexOutOfBoundsException(format("{} is greater or equal size {}.", index, size));
    try(InputStream stream = new FileInputStream(file)){
      stream.skip(offset+index);
      final int b = stream.read();
      if(b<0) throw new EOFException();
      return (byte) b;
    }
    catch (final IOException e) {throw wrap(e);}
  }


  @Override
  public byte[] toByteArray() {
    final byte[] result = new byte[size()];
    try(InputStream stream = new FileInputStream(file)){
      stream.skip(offset);
      int read = 0;
      while(read<result.length){
        final int n = stream.read(result, read, result.length-read);
        if(n==-1)throw new EOFException();
        read+=n;
        }
      return result;
    } catch (final IOException e) {throw wrap(e);}
  }

  @Override
  public Iterator<Byte> iterator() {
    return call(()->new StreamIterator(new FileInputStream(file)));
  }

  @Override
  public FileBytes subList(final long fromIndex, final long toIndex) {
    final long size = longSize();
    checkIndices(fromIndex, toIndex, size);
    if(fromIndex==0 && toIndex==size) return this;
    else{
      return new FileBytes(file, fromIndex, toIndex-fromIndex);
    }
  }

}
