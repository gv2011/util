package com.github.gv2011.util.download.imp;

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
