package com.github.gv2011.logbackadapter;

import static org.slf4j.LoggerFactory.getLogger;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

class LogbackadapterTest {

  private static final Logger LOG = getLogger(LogbackadapterTest.class);

  @Test
  void test() {
    LOG.info("test");
  }

}
