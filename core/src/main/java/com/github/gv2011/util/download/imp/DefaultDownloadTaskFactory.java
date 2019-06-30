package com.github.gv2011.util.download.imp;

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
