package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;
import static com.github.gv2011.util.ex.Exceptions.run;
import static com.github.gv2011.util.ex.Exceptions.wrap;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.AbstractList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.Pair;

import net.jcip.annotations.Immutable;

@Immutable
abstract class AbstractBytes extends AbstractList<Byte> implements Bytes{

  private static String HEX_CHARS = "0123456789ABCDEF";
  private final Constant<Integer> hashCodeCache = Constants.cachedConstant(super::hashCode);
  private final Constant<String> toStringCache = Constants.softRefConstant(this::toStringImp);
  private final Constant<Hash256> hashCache = Constants.cachedConstant(this::hashImp);


  @Override
  public String toString(){
    return toStringCache.get();
  }

  protected final void checkNotClosed() {
    if(closed()) throw new IllegalStateException("Closed.");
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
  public int getUnsigned(final long index) {
    return Byte.toUnsignedInt(get(index));
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
  public Pair<Bytes,Bytes> split(final long index){
    final Bytes b1 = this.subList(0L, index);
    final Bytes b2 = this.subList(index, longSize());
    return pair(b1, b2);
  }



  @Override
  public int write(final byte[] b, final int off, final int len) {
    final int result = (int) Math.min(len, longSize());
    for(int i=0; i<result; i++){b[off+i] = getByte(i);}
    return result;
  }

  @Override
  public void write(final OutputStream stream){
    run(()->{
      for(final byte b: this) stream.write(b);
    });
  }


  @Override
  public void write(final Path file) {
    checkNotClosed();
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
  public String toString(final Charset charset) {
    return new String(toByteArray(), charset);
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
    checkNotClosed();
    final MessageDigest md = call(()->MessageDigest.getInstance(Hash256.ALGORITHM));
    for(final byte b:this) md.update(b);
    return new Hash256Imp(md);
  }



  @Override
  public Hash256 asHash() {
    return new Hash256Imp(toByteArray());
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
  public Bytes toBase64() {
    checkNotClosed();
    try(final BytesBuilder builder = ByteUtils.newBytesBuilder()){
      final OutputStream stream = Base64.getEncoder().wrap(builder);
      write(stream);
      run(stream::close);
      return builder.build();
      }
  }

  @Override
  public Bytes decodeBase64() {
    try(final InputStream stream = Base64.getDecoder().wrap(openStream())){
      return ByteUtils.fromStream(stream);
    } catch (final IOException e) {throw wrap(e);}
  }

  @Override
  public Iterator<Byte> iterator() {
    checkNotClosed();
    return new It(0);
  }

  @Override
  public ListIterator<Byte> listIterator(final int index) {
    checkNotClosed();
    return new It(index);
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

  @Override
  public String toHexMultiline() {
    final StringBuilder sb = new StringBuilder();
    int column = 0;
    for(int i=0; i<size(); i++){
      if(column>0){
        if(column==32){
          sb.append('\n');
          column = 0;
        }
        else sb.append(' ');
      }
      sb.append(toHex(getUnsigned(i)));
      column++;
    }
    return sb.toString();
  }

  private String toHex(final int b) {
    if(b<0x10) return "0"+Integer.toHexString(b);
    else return Integer.toHexString(b);
  }

  @Override
  public Bytes append(final Bytes hashBytes) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public boolean startsWith(final Bytes prefix) {
    return startsWith(prefix, 0);
  }

  private boolean startsWith(final Bytes prefix, final long offset) {
    checkNotClosed();
    if(prefix.isEmpty()) return true;
    else if(prefix.longSize()>longSize()-offset) return false;
    else{
      boolean result = true;
      long i=0;
      while(result && i<prefix.longSize()){
        if(get(offset+i)!=prefix.get(i)) result = false;
        i++;
      }
      return result;
    }
  }

  @Override
  public Optional<Long> indexOfOther(final Bytes other) {
    checkNotClosed();
    Optional<Long> result = Optional.empty();
    boolean done = false;
    long searchIndex = 0;
    long remainingSize = longSize();
    final long otherSize = other.longSize();
    while(!done){
      if(remainingSize<otherSize){done = true;}
      else if(startsWith(other, searchIndex)){
        result = Optional.of(searchIndex);
        done = true;
      }
      else {searchIndex++; remainingSize--;}
    }
    return result;
  }


  @Override
  public int compareTo(final Bytes o) {
    if(equals(o)) return 0;
    else {
      int result = 0;
      long i = 0;
      while(result==0 && i<longSize() && i<o.longSize()) {
        result = getUnsigned(i)-o.getUnsigned(i);
        i++;
      }
      if(result==0) result = Long.signum(longSize()-o.longSize());
      assert result!=0;
      return result;
    }
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



}
