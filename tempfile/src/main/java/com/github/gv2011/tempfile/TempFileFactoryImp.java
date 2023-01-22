package com.github.gv2011.tempfile;

import java.nio.file.Files;
import java.nio.file.Path;

import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.log.LogAdapter;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;
import com.github.gv2011.util.tempfile.TempDir;
import com.github.gv2011.util.tempfile.TempFileFactory;

public final class TempFileFactoryImp implements TempFileFactory{

  private final Opt<Path> baseDir;

  public TempFileFactoryImp() {
    this(RecursiveServiceLoader.service(LogAdapter.class));
  }

  public TempFileFactoryImp(final LogAdapter logAdapter) {
    baseDir = logAdapter.tryGetLogFileDirectory().map(p->p.resolve("tmp"));
  }

  @Override
  public TempDir createTempDir() {
    return new TempDirImp(
      baseDir
      .map(d->{
        Files.createDirectories(d);
        return Files.createTempDirectory(d, "h2db-");
      })
      .orElseGet(()->Files.createTempDirectory("h2db-"))
    );
  }

}
