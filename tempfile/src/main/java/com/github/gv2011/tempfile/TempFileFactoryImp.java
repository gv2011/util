package com.github.gv2011.tempfile;

import static org.slf4j.LoggerFactory.getLogger;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;

import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.log.LogAdapter;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;
import com.github.gv2011.util.tempfile.TempDir;
import com.github.gv2011.util.tempfile.TempFileFactory;

public final class TempFileFactoryImp implements TempFileFactory{

  private static final Logger LOG = getLogger(TempFileFactoryImp.class);

  private static final String PREFIX = TempFileFactoryImp.class.getSimpleName()+"-";

  final Opt<Path> baseDir;

  public TempFileFactoryImp() {
    this(RecursiveServiceLoader.service(LogAdapter.class));
  }

  public TempFileFactoryImp(final LogAdapter logAdapter) {
    baseDir = logAdapter.tryGetLogFileDirectory().map(p->p.resolve("tmp"));
    LOG.info("Using base directory {}.", baseDir);
  }

  @Override
  public TempDir createTempDir() {
    return new TempDirImp(
      baseDir
      .map(d->{
        Files.createDirectories(d);
        return Files.createTempDirectory(d, PREFIX);
      })
      .orElseGet(()->Files.createTempDirectory(PREFIX))
    );
  }

}
