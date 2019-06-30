package com.github.gv2011.util.lock;

import com.github.gv2011.util.time.Clock;

public class DefaultLockFactory implements Lock.Factory{

  @Override
  public Lock create() {
    return new DefaultLock();
  }

  @Override
  public Lock create(final Clock clock) {
    return new DefaultLock(){
      @Override
      Clock getClock() {return clock;}
    };
  }

  @Override
  public <T> Latch<T> createLatch() {
    return new DefaultLatch<>(create());
  }

}
