package com.github.gv2011.util.lock;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2019 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

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
    }
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
