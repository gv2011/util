package com.github.gv2011.tempfile;

import static com.github.gv2011.util.icol.ICollections.single;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.github.gv2011.util.log.LogAdapter;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;
import com.github.gv2011.util.tempfile.TempDir;
import com.github.gv2011.util.tempfile.TempFiles;

class TempFilesTest {

  @Test
  void test() {
    assertThat(
      RecursiveServiceLoader.service(LogAdapter.class).tryGetLogConfiguration(),
      is(single(Paths.get("logback.xml").toUri()))
    );
    final Path expectedTmpDir = Paths.get("log", "tmp").toAbsolutePath();
    assertThat(
      ((TempFileFactoryImp)TempFiles.tempFileFactory()).baseDir.get().toAbsolutePath(),
      is(expectedTmpDir)
    );
    try(TempDir tempDir = TempFiles.createTempDir()){
      assertThat(tempDir.path().getParent(), is(expectedTmpDir));
    }
  }

}
