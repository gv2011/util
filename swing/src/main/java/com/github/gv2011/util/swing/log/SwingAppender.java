package com.github.gv2011.util.swing.log;

/*-
 * #%L
 * util-swing
 * %%
 * Copyright (C) 2018 Vinz (https://github.com/gv2011)
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.CachedConstant;
import com.github.gv2011.util.Constants;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

public class SwingAppender extends UnsynchronizedAppenderBase<ILoggingEvent>{

  private static final CachedConstant<SwingAppender> INSTANCE = Constants.cachedConstant();

  public static final AutoCloseableNt subscribe(final Consumer<ILoggingEvent> consumer){
    final SwingAppender instance = INSTANCE.get();
    synchronized(instance.lock) {
      instance.subscribers.add(consumer);
      for(final ILoggingEvent e: instance.store) consumer.accept(e);
      instance.store = new ArrayList<>();
    }
    return ()->instance.subscribers.remove(consumer);
  }

  private final Set<Consumer<ILoggingEvent>> subscribers = Collections.synchronizedSet(new HashSet<>());
  private final Object lock = new Object();
  private List<ILoggingEvent> store = new ArrayList<>();

  public SwingAppender() {
    INSTANCE.set(this);
  }

  @Override
  protected void append(final ILoggingEvent event) {
    if(started) {
      if(subscribers.isEmpty()) {
        synchronized(lock) {store.add(event);}
      }
      else subscribers.parallelStream().forEach(s->s.accept(event));
    };
  }

}
