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




import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.Builder;
import com.github.gv2011.util.FileUtils;
import com.github.gv2011.util.ann.NotThreadSafe;


@NotThreadSafe
public class BytesBuilder extends FilterOutputStream implements Builder<Bytes>, AutoCloseableNt{

  private final Logger LOG = getLogger(BytesBuilder.class);

  private long count=0;
  private int hashCode = 1;
  private final int limit = 0x10000;
  private Path tmpFile;
  private final AtomicBoolean closed = new AtomicBoolean(false);
  private Bytes result;

  private ByteArrayOutputStream bos;

  private DigestOutputStream digest;

  BytesBuilder(){
    super(null);
    bos = new ByteArrayOutputStream();
    setOut(bos);
  }

  BytesBuilder(final int initialBufferSize){
    super(null);
    bos = new ByteArrayOutputStream(initialBufferSize);
    setOut(bos);
  }



  private void setOut(final OutputStream stream) {
    digest = new DigestOutputStream(
      stream,
      call(()->Hash256.ALGORITHM.createMessageDigest())
    );
    out = digest;
  }



  @Override
  public void write(final byte[] b, final int off, final int len){
    checkSize(len);
    call(()->out.write(b, off, len));
    count+=len;
    final int end = off+len;
    for(int i=off; i<end; i++) hashCode = 31*hashCode + Byte.hashCode(b[i]);
  }

  @Override
  public void write(final byte b[]){
      write(b, 0, b.length);
  }

  public BytesBuilder append(final Bytes b){
    b.write(this);
    return this;
  }


  private void checkSize(final int len) {
    if(closed()) throw new IllegalStateException("Closed.");
    if(tmpFile==null){
      if(count+len>limit){
        call(()->{
          tmpFile = Files.createTempFile("buffer", ".bin");
          LOG.debug("Created tempFile {}.", tmpFile.toAbsolutePath());
          tmpFile.toFile().deleteOnExit();
          setOut(Files.newOutputStream(tmpFile));
          out.write(bos.toByteArray());
          bos = null;
        });
      }
    }
  }



  @Override
  public void write(final int b){
    checkSize(1);
    call(()->out.write(b));
    count++;
    hashCode = 31*hashCode + Byte.hashCode((byte)b);
  }




  @Override
  public Bytes build() {
    if(closed.getAndSet(true)==false){
      call(out::close);
      if(tmpFile!=null){
        result = new FileBackedBytesImp(tmpFile, count, hashCode, new Hash256Imp(digest.getMessageDigest()));
      }else{
        result = new ArrayBytes(bos.toByteArray());
        bos = null;
      }
    }
    return result;
  }

  public Bytes copy() {
    if(closed()) throw new IllegalStateException("Closed.");
    if(tmpFile!=null){
      final Path newTmp =
      call(()->{
        out.flush();
        final Path tmpFile2 = Files.createTempFile("buffer", ".bin");
        LOG.debug("Created tempFile {}.", tmpFile2.toAbsolutePath());
        tmpFile2.toFile().deleteOnExit();
        FileUtils.copy(tmpFile, tmpFile2);
        return tmpFile2;
      });
      result = new FileBackedBytesImp(newTmp, count, hashCode, new Hash256Imp(digest.getMessageDigest()));
    }else{
      result = new ArrayBytes(bos.toByteArray());
      bos = null;
    }
    return result;
  }


  @Override
  public void close(){
    build();
  }


//  public Optional<Bytes> remove(final Bytes marker) {
//    final byte[] bytes = bos.toByteArray();
//    bos.reset();
//    if(marker.isEmpty()){
//      if(bytes.length==0) return Optional.empty();
//      else return Optional.of(ByteUtils.newBytes(bytes));
//    }else{
//      final int markerStart = indexOf(bytes, marker);
//      if(markerStart==-1)  return Optional.empty();
//      else{
//        final int markerEnd = markerStart+marker.size();
//        bos.write(bytes, markerEnd, bytes.length-markerEnd); //Put the part after the marker back to
//        return Optional.of(ByteUtils.newBytes(bytes, 0, markerStart));
//      }
//    }
//  }



  @Override
  public boolean closed() {
    return closed.get();
  }





  /**
   * First index of marker sequence in bytes or -1 if not there.
   */
  @SuppressWarnings("unused")
  private int indexOf(final byte[] bytes, final Bytes marker) {
    verify(!marker.isEmpty());
    boolean done = false;
    int i=0;
    int result=-1;
    while(!done){
      if(bytes.length-i<marker.size()) done=true;
      else{
        boolean same = bytes[i]==marker.get(0);
        int j=1;
        while(same && j<marker.size()){
          same = bytes[i+j]==marker.get(j);
          j++;
        }
        if(same) {done=true; result = i;}
        else i++;
      }
    }
    return result;
  }



  public long size() {
    return count;
  }

  public boolean isEmpty(){
    return count==0L;
  }

}
