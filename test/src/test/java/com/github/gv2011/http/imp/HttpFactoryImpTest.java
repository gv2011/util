package com.github.gv2011.http.imp;

import static com.github.gv2011.util.icol.ICollections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import org.junit.Test;
import org.slf4j.Logger;

import com.github.gv2011.util.http.HttpServer;

public class HttpFactoryImpTest {
  
  private static final Logger LOG = getLogger(HttpFactoryImpTest.class);

  @Test
  public void testCreateServer() {
    try(HttpServer s = new HttpFactoryImp().createServer(emptyList(), 0)){
      LOG.info("Started.");
    }
    LOG.info("Stopped.");
  }

}
