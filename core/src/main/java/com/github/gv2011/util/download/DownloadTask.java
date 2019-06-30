package com.github.gv2011.util.download;

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
