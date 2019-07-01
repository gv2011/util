package com.github.gv2011.util.download;

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

import java.net.URI;
import java.nio.file.Path;
import java.util.function.Consumer;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.Constant;
import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.bytes.Hash256;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;

public interface DownloadTask extends AutoCloseableNt{

  public static final Constant<Factory> FACTORY = RecursiveServiceLoader.lazyService(Factory.class);

  public static Factory factory(){
    return FACTORY.get();
  }

  public static interface Factory{

    TmpFileDownloadTask create(
      final URI url, final Consumer<StatusInfo> statusListener, final Consumer<StatusInfo> onFinished
    );

    DownloadTask createUnzipTask(
      final URI url,
      final long size,
      final Hash256 hash,
      final Opt<String> stripRoot,
      final Consumer<StatusInfo> statusListener,
      final Consumer<StatusInfo> onFinished,
      final Path targetDirectory
    );

  }

  public static enum Status{INITIAL(false), RUNNING(false), FINISHED(true), CANCELLED(true), ERROR(true);
    private final boolean terminationStatus;
    private Status(final boolean terminationStatus) {
      this.terminationStatus = terminationStatus;
    }
    public boolean isTerminationStatus(){
      return terminationStatus;
    }
  }

  public static interface StatusInfo extends Bean{
    Status status();
    long bytesDone();
    String message();
  }

  public static interface TmpFileDownloadTask extends DownloadTask{
    void copyTo(Path file);
  }


  void start();

  boolean cancel();

  StatusInfo getStatusInfo();

  void setThrottle(float bytesPerSecond);

}
