package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.run;
import static com.github.gv2011.util.ex.Exceptions.wrap;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.AbstractList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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




  @Override
  public boolean equals(final Object o) {
    boolean result;
    if(o==this) result = true;
    else if(!(o instanceof Bytes)) result = listEquals(o);
    else{
      final Bytes other = (Bytes)o;
      final long size = longSize();
      if(size!=other.longSize()) result = false;
      else if(size<=Hash256.SIZE) result = listEquals(o);
      else result = hash().equals(other.hash());
    }
    return result;
  }

  protected final boolean listEquals(final Object o) {
    boolean result;
    if(o==this) result = true;
    else if(!(o instanceof List)) result = false;
    else{
      final List<?> other = (List<?>)o;
      final long size = longSize();
      if(size!=other.size()) result = false;
      else result = super.equals(o);
    }
    return result;
  }

  protected Hash256 hashImp() {
    final MessageDigest md = call(()->MessageDigest.getInstance(Hash256.ALGORITHM));
    for(final byte b:this) md.update(b);
    return new Hash256Imp(md);
  }



  @Override
  public int toInt() {
    final int size = size();
    if(size>4) throw new IllegalStateException();
    final boolean negative = size==0?false:getByte(0)<0;
    int result = negative?-1:0;
    for(final byte b: this){
      result = ((result<<8) & -0x100) | (b & 0xFF);
    }
    return result;
  }



  @Override
  public CloseableBytes toBase64() {
    try(final BytesBuilder builder = ByteUtils.newBytesBuilder()){
      final OutputStream stream = Base64.getEncoder().wrap(builder);
      write(stream);
      run(stream::close);
      return builder.build();
      }
  }

  @Override
  public CloseableBytes decodeBase64() {
    try(final InputStream stream = Base64.getDecoder().wrap(openStream())){
      return ByteUtils.fromStream(stream);
    } catch (final IOException e) {throw wrap(e);}
  }

  @Override
  public Iterator<Byte> iterator() {
    return new It(0);
  }

  @Override
  public ListIterator<Byte> listIterator(final int index) {
    return new It(index);
  }

  private final class It implements ListIterator<Byte> {
    private long index;
    private It(final long index) {
      this.index = index;
    }
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
    @Override
    public boolean hasPrevious() {
      return index>0;
    }
    @Override
    public Byte previous() {
      if(index==0)throw new NoSuchElementException();
      return get(--index);
    }
    @Override
    public int nextIndex() {
      final long next = index+1;
      if(next>Integer.MAX_VALUE) throw new TooBigException();
      return (int)next;
    }
    @Override
    public int previousIndex() {
      final long previous = index-1;
      if(previous>Integer.MAX_VALUE) throw new TooBigException();
      return (int)previous;
    }
    @Override
    public void remove() {
      throw new UnsupportedOperationException("Read-only");
    }
    @Override
    public void set(final Byte e) {
      throw new UnsupportedOperationException("Read-only");
    }
    @Override
    public void add(final Byte e) {
      throw new UnsupportedOperationException("Read-only");
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
