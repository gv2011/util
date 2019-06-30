package com.github.gv2011.util.lock;

import com.github.gv2011.util.icol.Opt;

final class DefaultLatch<T> implements Latch<T> {

  private final Lock lock;
  private Opt<T> released = Opt.empty();
  private boolean closed;

  DefaultLatch(final Lock lock) {
    this.lock = lock;
  }

  @Override
  public void close() {
    lock.run(()->closed=true, true);
  }

  @Override
  public void release(final T value) {
    lock.run(()->released=Opt.of(value), true);
  }

  @Override
  public T await() {
    return lock.get(()->{
      while(!(released.isPresent() || closed)) lock.await();
      return released.orElseThrow(()->new IllegalStateException("Latch closed."));
    });
  }

}
