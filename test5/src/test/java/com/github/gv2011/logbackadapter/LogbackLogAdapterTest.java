package com.github.gv2011.logbackadapter;

import org.junit.jupiter.api.Test;

import com.github.gv2011.util.log.LogAdapter;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;

class LogbackLogAdapterTest {

  @Test
  void test() {
    final LogAdapter logAdapter = RecursiveServiceLoader.service(LogAdapter.class);
    logAdapter.loggerFactory().getLogger(LogbackLogAdapterTest.class.getName()).info("Test");
  }

}
