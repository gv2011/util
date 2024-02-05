package com.github.gv2011.tempfile;

import static com.github.gv2011.util.icol.ICollections.single;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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
      Paths.get(".").toAbsolutePath().normalize(),
      is(Paths.get("/data/src/gv2011/util/test5").toAbsolutePath())
    );
    assertThat(
      RecursiveServiceLoader.service(LogAdapter.class).tryGetLogConfiguration(),
      is(single(Paths.get("logback.xml").toUri()))
    );
    assertThat(
      Paths.get(".").toAbsolutePath().normalize(),
      is(Paths.get("/data/src/gv2011/util/test5").toAbsolutePath())
    );
    assertThat(
      ((TempFileFactoryImp)TempFiles.tempFileFactory()).baseDir.get().toAbsolutePath(),
      is(Paths.get("log", "tmp").toAbsolutePath())
    );
    try(TempDir tempDir = TempFiles.createTempDir()){
      assertThat(tempDir.path().getParent(), is(Paths.get("log/tmp").toAbsolutePath()));
    }
  }

}
