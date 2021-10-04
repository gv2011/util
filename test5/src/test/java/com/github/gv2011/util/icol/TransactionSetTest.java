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
  public void testAdd() throws InterruptedException {
    final TransactionSet<String> ts = new TransactionSet<String>();
    final CountDownLatch latch1 = new CountDownLatch(1);
    final CountDownLatch latch2 = new CountDownLatch(2);
    new Thread(()->{
      call(supplier(latch1::await));
      final boolean added = ts.add("a", s->{
        LOG.info("First adding {}.", s);
        call(()->Thread.sleep(100));
      });
      LOG.info("First done (added: {}).", added);
      latch2.countDown();
    }).start();
    new Thread(()->{
      call(supplier(latch1::await));
      final boolean added = ts.add("a", s->{
        LOG.info("Second adding {}.", s);
        call(()->Thread.sleep(100));
      });
      LOG.info("Second done (added: {}).", added);
      latch2.countDown();
    }).start();
    Thread.sleep(100);
    latch1.countDown();
    latch2.await();
    LOG.info("Finished.");
  }

}
