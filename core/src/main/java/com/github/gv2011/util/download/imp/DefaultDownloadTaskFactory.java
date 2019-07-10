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

import java.net.URI;
import java.nio.file.Path;
import java.util.function.Consumer;

import com.github.gv2011.util.bytes.Hash256;
import com.github.gv2011.util.download.DownloadTask;
import com.github.gv2011.util.download.DownloadTask.StatusInfo;
import com.github.gv2011.util.icol.Opt;


public final class DefaultDownloadTaskFactory implements DownloadTask.Factory{

  @Override
  public DownloadTask.TmpFileDownloadTask create(
    final URI url, final Consumer<StatusInfo> statusListener, final Consumer<StatusInfo> onFinished
  ) {
    return new TmpFileDownloadTask(url, statusListener, onFinished);
  }

  @Override
  public DownloadTask createUnzipTask(
    final URI url, final long size, final Hash256 hash, final Opt<String> stripRoot,
    final Consumer<StatusInfo> statusListener, final Consumer<StatusInfo> onFinished, final Path targetDirectory
  ) {
    return new UnzipDownloadTask(url, size, hash, stripRoot, statusListener, onFinished, targetDirectory);
  }

}
