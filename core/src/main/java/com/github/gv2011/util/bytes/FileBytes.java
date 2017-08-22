package com.github.gv2011.util.bytes;

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
