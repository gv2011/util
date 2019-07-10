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
