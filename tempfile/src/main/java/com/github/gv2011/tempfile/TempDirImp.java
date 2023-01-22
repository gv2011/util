package com.github.gv2011.tempfile;

import static com.github.gv2011.util.ex.Exceptions.format;

import java.nio.file.Path;

import com.github.gv2011.util.FileUtils;
import com.github.gv2011.util.tempfile.TempDir;

final class TempDirImp implements TempDir{

  private final Path dir;

  TempDirImp(final Path dir){
    this.dir = dir;
  }

  @Override
  public Path path() {
    return dir;
  }

  @Override
  public void close() {
    FileUtils.delete(dir);
  }

  @Override
  public String toString() {
    return format("{}[{}]", TempDir.class.getSimpleName(), dir);
  }

}
