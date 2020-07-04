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




import static org.slf4j.LoggerFactory.getLogger;

import java.nio.file.Path;

import org.slf4j.Logger;

import com.github.gv2011.util.FileUtils;


final class FileBackedBytesImp extends FileBytes{

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
