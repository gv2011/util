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
import static com.github.gv2011.util.ex.Exceptions.call;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.CachedConstant;
import com.github.gv2011.util.Constants;

import ch.qos.logback.core.OutputStreamAppender;

public class SwingAppender3<E> extends OutputStreamAppender<E>{

  private static final CachedConstant<SwingAppender3<?>> INSTANCE = Constants.cachedConstant();

  public static final SwingAppender3<?> instance(){return INSTANCE.get();}

  private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
  private boolean stop = false;
  private final Object lock = new Object();

  public SwingAppender3() {
    INSTANCE.set(this);
  }

  @Override
  public void start() {
    setOutputStream(bytes);
    super.start();
  }

  @Override
  public void stop() {
    synchronized(lock) {
      stop  = true;
      lock.notifyAll();
    }
  }

  @Override
  public void doAppend(final E eventObject) {
    synchronized(lock) {
      super.doAppend(eventObject);
      lock.notifyAll();
    }
  }

  public AutoCloseableNt addSubscriber(final Consumer<String> subscriber) {
    final AtomicBoolean close = new AtomicBoolean();
    synchronized(lock) {
      final Thread notifier = new Thread(()->notifyLoop(subscriber, close));
      notifier.setName(SwingAppender3.class.getSimpleName());
      notifier.start();
      lock.notifyAll();
    }
    return ()->{synchronized(lock) {close.set(true); lock.notifyAll();}};
  }

  private void notifyLoop(final Consumer<String> subscriber, final AtomicBoolean close){
    boolean stop = close.get();
    String text = "";
    while(!stop) {
      String newText = "";
      synchronized(lock) {
        boolean nothingChanged = true;
        while(nothingChanged) {
          stop = this.stop || close.get();
          if(stop) nothingChanged = false;
          else {
            newText = call(()->bytes.toString(UTF_8.name()));
            nothingChanged = !stop && newText.equals(text);
          }
          if(nothingChanged)call(()->lock.wait());
        }
      }
      if(!stop) {
        if(!newText.equals(text)) {
          text = newText;
          subscriber.accept(text);
        }
      }
    }
  }

}
