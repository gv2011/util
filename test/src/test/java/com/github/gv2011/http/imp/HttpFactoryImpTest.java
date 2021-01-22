package com.github.gv2011.http.imp;

import static com.github.gv2011.util.icol.ICollections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.OptionalInt;

import org.junit.Test;
import org.slf4j.Logger;

import com.github.gv2011.util.http.HttpFactory;
import com.github.gv2011.util.http.HttpServer;
import com.github.gv2011.util.icol.Opt;

public class HttpFactoryImpTest {
  
  private static final Logger LOG = getLogger(HttpFactoryImpTest.class);

  @Test
  public void testCreateServer() {
    try(HttpServer s = new HttpFactoryImp().createServer(
        emptyList(), 
        HttpFactory.SERVER_SELECTS_PORT, 
        Opt.empty(),
        h->false,
        OptionalInt.empty()
    )){
      LOG.info("Started.");
    }
    LOG.info("Stopped.");
  }

}
