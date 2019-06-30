package com.github.gv2011.util.lock;

import static com.github.gv2011.util.ex.Exceptions.call;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.gv2011.util.time.Clock;

class DefaultLock implements Lock{

  private final Object internalLock = new Object();

  @Override
  public final void run(final Runnable operation, final boolean notify) {
    assert !Thread.holdsLock(this);
    synchronized(internalLock){
      operation.run();
      if(notify) internalLock.notifyAll();
    };
  }

  @Override
  public final <T> T get(final Supplier<T> operation) {
    assert !Thread.holdsLock(this);
    synchronized(internalLock){
      return operation.get();
    }
  }

  @Override
  public final <A, R> R apply(final A argument, final Function<A, R> operation) {
    assert !Thread.holdsLock(this);
    synchronized(internalLock){
      return operation.apply(argument);
    }
  }

  @Override
  public final boolean isLocked() {
    assert !Thread.holdsLock(this);
    return Thread.holdsLock(internalLock);
  }

  @Override
  public final void publish() {
    assert !Thread.holdsLock(this);
    synchronized(internalLock){
      internalLock.notifyAll();
    }
  }

  @Override
  public final void await() {
    assert !Thread.holdsLock(this);
    synchronized(internalLock){
      call(()->internalLock.wait());
    }
  }

  @Override
  public final void await(final Duration timeOut) {
    assert !Thread.holdsLock(this);
    getClock().notifyAfter(internalLock, timeOut);
    synchronized(internalLock){
      call(()->internalLock.wait());
    }
  }

  Clock getClock(){
    return Clock.INSTANCE.get();
  }

}
