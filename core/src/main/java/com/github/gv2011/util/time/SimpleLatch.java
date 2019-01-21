package com.github.gv2011.util.time;

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
