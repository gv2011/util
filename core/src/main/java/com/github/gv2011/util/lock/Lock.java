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

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;
import com.github.gv2011.util.time.Clock;

public interface Lock {

  public static final Constant<Factory> FACTORY = RecursiveServiceLoader.lazyService(Factory.class);

  public static interface Factory{
    default Lock create(){
      return create(Clock.INSTANCE.get());
    }
    Lock create(Clock clock);
    <T> Latch<T> createLatch();
  }

  public static Lock create(){
    return FACTORY.get().create();
  }

  default void run(final Runnable operation){
    run(operation, false);
  }

  void run(Runnable operation, boolean notify);

  boolean isLocked();

  void publish();

  <T> T get(Supplier<T> operation);

  void await();

  void await(final Duration timeOut);

  <A,R> R apply(A argument, Function<A,R> operation);

}
