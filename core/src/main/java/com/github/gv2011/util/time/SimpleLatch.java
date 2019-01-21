package com.github.gv2011.util.time;

import static com.github.gv2011.util.ex.Exceptions.call;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SimpleLatch {

  private static final Logger LOG = LoggerFactory.getLogger(SimpleLatch.class);

  private static final Duration LOG_TICK_PERIOD = Duration.ofSeconds(10);

  public static SimpleLatch create(){
    return new SimpleLatch();
  }

  private final Object lock = new Object();
  private boolean released;

  private SimpleLatch(){}

  public void release(){
    synchronized(lock){
      if(!released){
        released = true;
        lock.notifyAll();
      }
    }
  }

  public void await(){
    synchronized(lock){
      while(!released) {
        LOG.debug("Waiting for release.");
        Clock.INSTANCE.get().notifyAfter(lock, LOG_TICK_PERIOD);
        call(()->lock.wait());
      }
    }
  }

  public boolean await(final Duration timeout){
    final Clock clock = Clock.INSTANCE.get();
    Instant now = clock.instant();
    final Instant limit = now.plus(timeout);
    synchronized(lock){
      while(!released && now.isBefore(limit)) {
        LOG.debug("Waiting for release or timeout.");
        Clock.INSTANCE.get().notifyAfter(lock, LOG_TICK_PERIOD);
        call(()->lock.wait());
        now = clock.instant();
      }
      return released;
    }
  }

}
