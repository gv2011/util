package com.github.gv2011.util.swing.imp.log;

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
    }
  }

}
