package com.github.gv2011.util.log;

import static com.github.gv2011.util.icol.ICollections.single;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;

class LogAdapterTest {

  @Test
  void testEnsureInitialized() {
    RecursiveServiceLoader.service(LogAdapter.class).ensureInitialized();
  }

  @Test
  void tryGetLogConfiguration() {
    assertThat(
      RecursiveServiceLoader.service(LogAdapter.class).tryGetLogConfiguration(),
      is(single(Paths.get("logback.xml").toUri()))
    );
  }

}
