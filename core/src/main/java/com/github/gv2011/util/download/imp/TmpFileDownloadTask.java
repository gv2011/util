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

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;

import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import com.github.gv2011.util.download.DownloadTask;


final class TmpFileDownloadTask extends AbstractDownloadTask implements DownloadTask.TmpFileDownloadTask{

  private final Path tmpFile;
  private final URI url;

  TmpFileDownloadTask(final URI url, final Consumer<StatusInfo> statusListener, final Consumer<StatusInfo> onFinished) {
    super(statusListener, onFinished);
    this.url = url;
    tmpFile = call(()->Files.createTempFile(toString(), ""));
  }

  @Override
  public void copyTo(final Path file) {
    call(()->Files.copy(tmpFile, file));
  }

  @Override
  boolean run(){
    return callWithCloseable(
      call(url::toURL)::openStream,
      in->{
        boolean finished = false;
        try(final OutputStream out = Files.newOutputStream(tmpFile)){
          final byte[] buffer = new byte[8192];
          while(!getStatusInfo().status().isTerminationStatus()){
            final int count = in.read(buffer);
            if(count==-1) finished = true;
            else{
              out.write(buffer, 0, count);
              total.addAndGet(count);
              lock.publish();
            }
          }
          return finished;
        }
      }
    );
  }
}
