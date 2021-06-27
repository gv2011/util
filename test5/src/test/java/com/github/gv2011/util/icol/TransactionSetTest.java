package com.github.gv2011.util.icol;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.supplier;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;


public class TransactionSetTest {

  private static final Logger LOG = getLogger(TransactionSetTest.class);

  @Test
  public void testAdd() {
    final TransactionSet<String> ts = new TransactionSet<String>();
    final CountDownLatch latch1 = new CountDownLatch(1);
    new Thread(()->{
      ts.add("a", s->{
        LOG.info("First adding {}.", s);
        call(supplier(latch1::await));
      });
      LOG.info("First done.");
    }).start();
    new Thread(()->{
      ts.add("a", s->{
        LOG.info("Second adding {}.", s);
        call(supplier(latch1::await));
      });
      LOG.info("Second done.");
    }).start();
    latch1.countDown();
  }

}
