package com.github.gv2011.tempfile;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class TempFileFactoryImpTest {

  @Test
  void testBaseDir() {
    assertThat(
      new TempFileFactoryImp().baseDir.get().toAbsolutePath(),
      is(Paths.get("log", "tmp").toAbsolutePath())
    );
  }

}
