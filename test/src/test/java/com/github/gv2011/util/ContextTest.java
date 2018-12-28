package com.github.gv2011.util;

/*-
 * #%L
 * util-test
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
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
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import org.junit.Test;
import org.slf4j.Logger;

public class ContextTest {

  private static final Logger LOG = getLogger(ContextTest.class);

  private final AtomicReference<AssertionError> lastException = new AtomicReference<>();

  private final InheritableThreadLocal<String> contextVar = new InheritableThreadLocal<>();
  private final InheritableThreadLocal<String> contextVar2 = new InheritableThreadLocal<>();

  @Test
  public void test() throws Exception{
    contextVar.set("initial");
    contextVar2.set("initial");
    final Thread t1 = new Thread(()->testWrapper("test1"), "outer1");
    final Thread t2 = new Thread(()->testWrapper("test2"), "outer2");
    t1.start();
    t2.start();
    t1.join();
    t2.join();
  }


  private void testWrapper(final String name){
    final ThreadGroup threadGroup = new ThreadGroup(name) {
      @Override
      public void uncaughtException(final Thread t, final Throwable e) {
        final AssertionError ex = new AssertionError(format("Uncaught exception in {}:", t), e) ;
        final Optional<AssertionError> old = Optional.ofNullable(lastException.getAndSet(ex));
        old.ifPresent(o->LOG.error(format("Exception hidden by next one:"), o));
      }
    };
    final Thread main = new Thread(
      threadGroup,
      ()->{
        contextVar.set(name);
        final ForkJoinPool pool = createPool(threadGroup);
        runTestInPool(pool, this::doTest);
        shutdown(pool);
      },
      "test-main",
      0,
      false
    );
    main.start();
    call(()->main.join());
  }

  private void shutdown(final ForkJoinPool pool) {
    pool.shutdown();
    verify(call(()->pool.awaitTermination(10, TimeUnit.SECONDS)));
  }

  private void runTestInPool(final ForkJoinPool pool, final Runnable test) {
    final ForkJoinTask<?> task = pool.submit(test);
    call(()->task.get());
  }

  private ForkJoinPool createPool(final ThreadGroup threadGroup) {
    final int parallelism = 4;
    final ForkJoinWorkerThreadFactory threadFactory = p->newThread(p, threadGroup);
    final UncaughtExceptionHandler handler = null;
    final boolean asyncMode = false;
    final ForkJoinPool pool = new ForkJoinPool(parallelism, threadFactory, handler, asyncMode);
    return pool;
  }

  private final ForkJoinWorkerThread newThread(final ForkJoinPool pool, final ThreadGroup threadGroup) {
    final ForkJoinWorkerThread t = new ForkJoinWorkerThread(pool) {};
    t.setDaemon(false);
    return t;
  };

  private final void doTest() {
      IntStream.range(0,100).parallel()
      .mapToObj(i->{
        final Thread t = Thread.currentThread();
        return
          t.getName()+
          (t.isDaemon()?"(d)":"")+"/"+
          t.getThreadGroup().getName()+":"+
          contextVar.get()+","+
          contextVar2.get()
        ;
      })
      .collect(toISet())
      .stream().forEach(System.out::println);
  }
}
