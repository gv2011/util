package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.run;
import static com.github.gv2011.util.ex.Exceptions.wrap;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;

import net.jcip.annotations.Immutable;

@Immutable
abstract class AbstractBytes extends AbstractList<Byte> implements Bytes{

  private static String HEX_CHARS = "0123456789ABCDEF";
  private final Constant<Integer> hashCodeCache = Constants.newConstant(super::hashCode);
  private final Constant<String> toStringCache = Constants.newCachedConstant(this::toStringImp);
  private final Constant<Hash256> hashCache = Constants.newConstant(this::hashImp);


  @Override
  public String toString(){
    return toStringCache.get();
  }

  protected String toStringImp(){
    final int s = size();
    final char[] result = new char[s==0?0:s*3-1];
    int i=0;
    for(final byte b: this){
      result[i*3] = HEX_CHARS.charAt((b>>4) & 0xF);
      result[i*3+1] = HEX_CHARS.charAt(b & 0xF);
      if(i<s-1)result[i*3+2] = ' ';
      i++;
    }
    return new String(result);
  }

  @Override
  public final int size() {
    final long size = longSize();
    if(size>Integer.MAX_VALUE) throw new TooBigException();
    return (int)size;
  }

  @Override
  public final Byte get(final int index) {
    return getByte(index);
  }

  public byte getByte(final int index){
    return get((long)index);
  }

  @Override
  public byte[] toByteArray(){
    final byte[] result = new byte[size()];
    int i=0;
    for(final byte b: this) result[i++]=b;
    return result;
  }

  @Override
  public Bytes subList(final int fromIndex, final int toIndex){
    return subList((long)fromIndex, (long)toIndex);
  }

  @Override
  public void write(final OutputStream stream){
    run(()->{
      for(final byte b: this) stream.write(b);
    });
  }


  @Override
  public void write(final Path file) {
    try(OutputStream stream = Files.newOutputStream(file, CREATE, TRUNCATE_EXISTING)) {
      write(stream);
    } catch (final IOException e) {throw wrap(e);}
  }

  @Override
  public int hashCode() {
     return hashCodeCache.get();
  }

  @Override
  public String utf8ToString() throws TooBigException {
    return new String(toByteArray(), UTF_8);
  }



  @Override
  public Hash256 hash() {
    return hashCache.get();
  }


  protected Hash256 hashImp() {
    final MessageDigest md = call(()->MessageDigest.getInstance("SHA-256"));
    for(final byte b:this) md.update(b);
    return new Hash256Imp(md);
  }

  @Override
  public Iterator<Byte> iterator() {
    return new It();
  }

  private final class It implements Iterator<Byte> {
    private long index=0;
    @Override
    public boolean hasNext() {
      return index<longSize();
    }
    @Override
    public Byte next() {
      try {return get(index++);}
      catch (final IndexOutOfBoundsException e) {
        throw new NoSuchElementException();
      }
    }
}


  protected final static void checkIndices(final long fromIndex, final long toIndex, final long size) {
    if(
      fromIndex>size ||
      toIndex>size   ||
      fromIndex < 0  ||
      toIndex < 0
    ) throw new IndexOutOfBoundsException();
    if(fromIndex>toIndex) throw new IllegalArgumentException();
  }


}
