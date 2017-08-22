package com.github.gv2011.util.bytes;

import static org.slf4j.LoggerFactory.getLogger;

import java.nio.file.Path;

import org.slf4j.Logger;

import com.github.gv2011.util.FileUtils;


final class FileBackedBytesImp extends FileBytes implements Bytes{

  private static final Logger LOG = getLogger(FileBackedBytesImp.class);
  private final Hash256 hash;
  private final int hashCode;
  private volatile boolean closed;

  FileBackedBytesImp(final Path file, final long size, final int hashCode, final Hash256 hash) {
    super(file, 0, size);
    this.hashCode = hashCode;
    this.hash = hash;
  }

  @Override
  protected void finalize() throws Throwable {
    close();
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
    closed = true;
    FileUtils.deleteFile(file());
    LOG.debug("File {} deleted.", file().toAbsolutePath());
  }

  @Override
  public boolean closed() {
    return closed;
  }

  public Bytes loadInMemory() {
    return new ArrayBytes(toByteArray());
  }

}
