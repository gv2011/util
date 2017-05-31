package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.ex.Exceptions.call;
import static org.slf4j.LoggerFactory.getLogger;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;


class FileBackedBytesImp extends FileBytes implements CloseableBytes{

  private static final Logger LOG = getLogger(FileBackedBytesImp.class);
  private final Hash256 hash;
  private final int hashCode;

  FileBackedBytesImp(final Path file, final long size, final int hashCode, final Hash256 hash) {
    super(file, 0, size);
    this.hashCode = hashCode;
    this.hash = hash;
  }


  @Override
  public Hash256 hash() {
    return hash;
  }


  @Override
  protected Hash256 hashImp() {
    return hash;
  }



  @Override
  public int hashCode() {
    return hashCode;
  }


  @Override
  public void close() {
    final boolean deleted = call(()->Files.deleteIfExists(file));
    if(deleted) LOG.debug("File {} deleted.", file.toAbsolutePath());
  }


  @Override
  public Bytes loadInMemory() {
    return new ArrayBytes(toByteArray());
  }

}
