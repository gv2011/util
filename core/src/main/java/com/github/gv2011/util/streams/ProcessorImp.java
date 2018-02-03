package com.github.gv2011.util.streams;

import static com.github.gv2011.util.ex.Exceptions.call;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
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

import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import com.github.gv2011.util.bytes.ByteUtils;

//TODO WIP
final class ProcessorImp implements Future<Long> {

  private final Thread                thread;
  private final InputStream           stream;
  private final Consumer<StreamEvent> eventHandler;
  @SuppressWarnings("unused")
  private final Object                lock = new Object();
  private boolean                     cancelled;
  @SuppressWarnings("unused")
  private boolean                     done;

  ProcessorImp(final InputStream stream, final Consumer<StreamEvent> eventHandler) {
    this.stream = stream;
    this.eventHandler = eventHandler;
    thread = Executors.defaultThreadFactory().newThread(this::process);
    thread.start();
  }

  @SuppressWarnings("unused")
  private void process() {
    try {
      try {
        final byte[] buffer = new byte[8192];
        final int validInBuffer = 0;
        long count = 0;
        final boolean done = false;
        while (!done) {
          if (count > 0) {
            eventHandler.accept(StreamEventImp.data(ByteUtils.newBytes(buffer, 0, (int) count)));
            count = 0;
          }
          if (cancelled) count = -1;
          else {
            try {
              count = stream.read(buffer);
            } catch (final IOException e) {
              count = -1;
              if (!cancelled) throw e; // assuming the exception is consequence
                                       // of cancelling.
            }
          }
        }
        // eventHandler.accept(cancelled?StreamEventImp.cancelled() :
        // StreamEventImp.eos(ByteUtils.emptyBytes()));
      } finally {
        stream.close();
      }
    } catch (final Throwable e) {
      eventHandler.accept(StreamEventImp.error(e, ByteUtils.emptyBytes()));
    }
  }

  @Override
  public boolean isCancelled() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public boolean isDone() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Long get() throws InterruptedException, ExecutionException {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Long get(final long timeout, final TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public boolean cancel(final boolean mayInterrupt) {
    cancelled = true;
    call(stream::close);
    return false;
  }
}
