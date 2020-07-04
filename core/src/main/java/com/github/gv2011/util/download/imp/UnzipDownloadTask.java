package com.github.gv2011.util.download.imp;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2019 Vinz (https://github.com/gv2011)
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
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.github.gv2011.util.FileUtils;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.StreamUtils;
import com.github.gv2011.util.StreamUtils.Throttler;
import com.github.gv2011.util.StringUtils;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Hash256;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.streams.SimpleThrottler;


final class UnzipDownloadTask extends AbstractDownloadTask{

  private final Path targetDirectory;
  private final URI url;
  private final Opt<String> stripRoot;
  private final long size;
  private final Hash256 hash;

  UnzipDownloadTask(
    final URI url, final long size, final Hash256 hash, final Opt<String> stripRoot,
    final Consumer<StatusInfo> statusListener, final Consumer<StatusInfo> onFinished, final Path targetDirectory
  ) {
    super(statusListener, onFinished);
    this.url = url;
    this.size = size;
    this.hash = hash;
    this.stripRoot = stripRoot;
    this.targetDirectory = targetDirectory;
    stripRoot.ifPresent(sr->verify(sr, s->s.endsWith("/")));
    verifyEqual(targetDirectory.toString(), call(()->targetDirectory.toRealPath()).toString());
    verify(FileUtils.isEmpty(targetDirectory));
  }

  @Override
  boolean run(){
    return callWithCloseable(()->url.toURL().openStream(), in->{
        final Throttler throttler = new SimpleThrottler(this::getThrottle);
        final Pair<InputStream, Supplier<Hash256>> hashStream =
          ByteUtils.hashStream(
            StreamUtils.countingStream(in, c->total.addAndGet(c), throttler)
          )
        ;
        final InputStream hashed = hashStream.getKey();
        final ZipInputStream zis = new ZipInputStream(hashed);
        boolean cancelled = false;
        final byte[] buffer = new byte[8192];
        Opt<ZipEntry> zipEntry = Opt.ofNullable(zis.getNextEntry());
        while(zipEntry.isPresent() && !cancelled){
          try{
            final ZipEntry ze = zipEntry.get();
            final String path = stripRoot
              .map(p->StringUtils.removePrefix(ze.getName(), p))
              .orElseGet(ze::getName)
            ;
            if(ze.isDirectory()) createDirectory(path);
            else{
              try(OutputStream fileOut = createFileOut(path)){
                boolean doStep = true;
                while(doStep){
                  if(getStatus().isTerminationStatus()) {
                    cancelled = true;
                    doStep = false;
                  }
                  else{
                    final int count = zis.read(buffer);
                    if(count==-1) doStep = false;
                    else{
                      fileOut.write(buffer, 0, count);
                    }
                  }
                }
              }
            }
          }
          finally{
            zis.closeEntry();
          }
          zipEntry = Opt.ofNullable(zis.getNextEntry());
        }

        if(!cancelled){
          while(hashed.read(buffer)>0){}
          verifyEqual(total.get(), size);
          verifyEqual(hashStream.getValue().get(), hash);
        }
        return !cancelled;
      }
    );
  }

  private void createDirectory(final String path) {
    final Path resolved = targetDirectory.resolve(path);
    verify(resolved, r->r.startsWith(targetDirectory));
    call(()->Files.createDirectories(resolved));
  }

  private OutputStream createFileOut(final String path) {
    final Path resolved = targetDirectory.resolve(path);
    verify(FileUtils.isInside(resolved, targetDirectory));
    return call(()->Files.newOutputStream(resolved));
  }
}
