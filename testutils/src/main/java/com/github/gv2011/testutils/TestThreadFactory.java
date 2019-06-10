package com.github.gv2011.testutils;

/*-
 * #%L
 * util-test
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
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.format;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.ex.ThrowingRunnable;
import com.github.gv2011.util.icol.Opt;

public final class TestThreadFactory implements ThreadFactory, AutoCloseableNt{

  private static final Logger LOG = LoggerFactory.getLogger(TestThreadFactory.class);

  private final Map<Thread,ThrowingRunnable> threads = new HashMap<>();
  private boolean error = false;

  @Override
  public void close() {
    closeInternal();
    synchronized(threads) {assertFalse(error);}
  }

  private void closeInternal() {
    boolean done = false;
    while(!done) {
      Opt<Entry<Thread,ThrowingRunnable>> thread;
      synchronized(threads) {
        thread = threads.isEmpty() ? Opt.empty() : Opt.of(threads.entrySet().iterator().next());
        thread.ifPresent(t->threads.remove(t.getKey()));
      }
      if(!thread.isPresent()) done = true;
      else{
        final Entry<Thread,ThrowingRunnable> t = thread.get();
        try {
          if(t.getKey().isAlive()) {
            LOG.info("Terminating thread {}.", t.getKey().getName());
            t.getValue().run();
            t.getKey().join();
          }
        } catch (final Throwable e) {
          synchronized(threads) {error = true;}
          LOG.error(format("Could not terminate {}.", t), e);
        }
      }
    }
  }

  @Override
  public Thread newThread(final Runnable r) {
    return newThread(r, Opt.empty());
  }

  public Thread newThread(final Runnable r, final ThrowingRunnable terminator) {
    return newThread(r, Opt.of(terminator));
  }

  private Thread newThread(final Runnable r, final Opt<ThrowingRunnable> terminator) {
    final Thread thread = new Thread(r);
    synchronized(threads) {
      verify(!error);
      threads.put(thread, terminator.orElse(thread::interrupt));
    }
    thread.setUncaughtExceptionHandler(this::uncaughtException);
    return thread;
  }

  private void uncaughtException(final Thread t, final Throwable e) {
    LOG.error(format("Uncaught exception in thread {}:", t.getName()), e);
    boolean first;
    synchronized(threads) {
      first = !error;
      error = true;
    }
    if(first) {
      LOG.info("Closing (failfast) because of uncaught exception.");
      new Thread(this::closeInternal, "failfast").start();
    }
  }
}
