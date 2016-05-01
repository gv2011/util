package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.run;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.Builder;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
class BytesBuilder extends FilterOutputStream implements Builder<CloseableBytes>, AutoCloseableNt{

  private final Logger LOG = getLogger(BytesBuilder.class);

  private long count=0;
  private int hashCode = 1;
  private final int limit=8192;
  private Path tmpFile;
  private final AtomicBoolean closed = new AtomicBoolean(false);
  private CloseableBytes result;

  private ByteArrayOutputStream bos;

  private DigestOutputStream digest;

  BytesBuilder(){
    super(null);
    bos = new ByteArrayOutputStream();
    setOut(bos);
  }



  private void setOut(final OutputStream stream) {
    digest = new DigestOutputStream(
      stream,
      call(()->MessageDigest.getInstance(Hash256.ALGORITHM))
    );
    out = digest;
  }



  @Override
  public void write(final byte[] b, final int off, final int len){
    checkSize(len);
    run(()->out.write(b, off, len));
    count+=len;
    final int end = off+len;
    for(int i=off; i<end; i++) hashCode = 31*hashCode + Byte.hashCode(b[i]);
  }

  public BytesBuilder append(final Bytes b){
    b.write(this);
    return this;
  }


  private void checkSize(final int len) {
    if(closed.get()) throw new IllegalStateException("Closed.");
    if(tmpFile==null){
      if(count+len>limit){
        run(()->{
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
    run(()->out.write(b));
    count++;
    hashCode = 31*hashCode + Byte.hashCode((byte)b);
  }




  @Override
  public CloseableBytes build() {
    if(closed.getAndSet(true)==false){
      run(out::close);
      if(tmpFile!=null){
        result = new FileBackedBytesImp(tmpFile, count, hashCode, new Hash256Imp(digest.getMessageDigest()));
      }else{
        result = new CloseableArrayBytes(bos.toByteArray());
        bos = null;
      }
    }
    return result;
  }

  @Override
  public void close(){
    build();
  }


  private static final class CloseableArrayBytes extends ArrayBytes implements CloseableBytes {
    private CloseableArrayBytes(final byte[] bytes) {
      super(bytes);
    }
    @Override
    public void close() {}
  }

}
