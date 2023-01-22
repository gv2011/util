package com.github.gv2011.tempfile;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.github.gv2011.util.tempfile.TempDir;
import com.github.gv2011.util.tempfile.TempFiles;

class TempFilesTest {

  @Test
  void test() {
    try(TempDir tempDir = TempFiles.createTempDir()){
      assertThat(tempDir.path().getParent(), is(Paths.get("log/tmp").toAbsolutePath()));
    }
  }

}
