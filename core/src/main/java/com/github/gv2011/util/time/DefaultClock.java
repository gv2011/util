package com.github.gv2011.util.time;

import static com.github.gv2011.util.CollectionUtils.tryGetFirstKey;
import static com.github.gv2011.util.Verify.verify;

/*-
 * #%L
 * The MIT License (MIT)
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

import static com.github.gv2011.util.ex.Exceptions.call;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.Comparison;
import com.github.gv2011.util.icol.Opt;

public final class DefaultClock implements Clock, AutoCloseableNt {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultClock.class);

  private static final Duration LOG_TICK_PERIOD = Duration.ofSeconds(10);

  private final Object lock = new Object();
  private final Thread thread;

  private boolean closing;

  private final SortedMap<Instant, List<Object>> notifications = new TreeMap<>();

  public DefaultClock() {
    thread = new Thread(this::run, "clock");
    thread.start();
  }

  @Override
  public void await(final Instant instant) {
    final Object awaitLock = new Object();
    Instant now = instant();
    while (now.isBefore(instant)) {
      synchronized(awaitLock){
        notifyAt(awaitLock, instant, now);
      }
      now = instant();
    }
  }

  @Override
  public Instant instant() {
    return Instant.now();
  }

  @Override
  public void sleep(final Duration sleepTime) {
    await(instant().plus(sleepTime));
  }

  @Override
  public void notifyAfter(final Object obj, final Duration duration) {
    final Instant now = instant();
    notifyAt(obj, now.plus(duration), now);
  }

  @Override
  public void notifyAt(final Object obj, final Instant notifyAt) {
    notifyAt(obj, notifyAt, instant());
  }

  private void notifyAt(final Object obj, final Instant notifyAt, final Instant now) {
    if (!notifyAt.isAfter(now)) {
      synchronized (obj) {
        obj.notifyAll();
      }
    } else {
      synchronized (lock) {
        final Opt<Instant> actualNext = tryGetFirstKey(notifications);
        notifications.computeIfAbsent(notifyAt, t -> new ArrayList<>()).add(obj);
        if (actualNext.map(n -> notifyAt.isBefore(n)).orElse(true)) {
          LOG.debug("Next notification at {}.", notifyAt);
          wakeupClock();
        }
      }
    }
  }

  private void wakeupClock() {
    assert Thread.holdsLock(lock);
    thread.interrupt();
  }

  private void run() {
    boolean shouldRun = true;
    while (shouldRun) {
      final Instant now = instant();
      final Instant sleepUntil;
      synchronized (lock) {
        if (closing) {
          shouldRun = false;
          LOG.debug("Closing, notifying all.");
          notifications.values().parallelStream().flatMap(List::parallelStream).forEach(Object::notifyAll);
          sleepUntil = now;
        } else {
          Opt<Instant> nextNotification = tryGetFirstKey(notifications);
          while (nextNotification.map(n->!now.isBefore(n)).orElse(false)) {
            LOG.debug("Notifying.");
            notifications.remove(nextNotification.get()).parallelStream().forEach(Object::notifyAll);
            nextNotification = tryGetFirstKey(notifications);
          }
          nextNotification.ifPresent(n->verify(now.isBefore(n)));
          sleepUntil = nextNotification.orElseGet(()->now.plus(LOG_TICK_PERIOD));
        }
      }
      sleepUntilInternal(sleepUntil);
    }
  }

  private void sleepUntilInternal(final Instant t) {
    assert !Thread.holdsLock(lock);
    Duration duration = Duration.between(instant(), t);
    while (!duration.isNegative() && !duration.isZero()) {
      try {
        LOG.debug("Going to sleep for {}.", duration);
        Thread.sleep(duration.toMillis());
      } catch (final InterruptedException e) {
        Thread.interrupted(); // clear interrupted status
        LOG.debug("Sleep interrupted.", duration);
      }
      duration = Comparison.max(Duration.between(instant(), t), LOG_TICK_PERIOD);
    }
  }

  @Override
  public void close() {
    synchronized (lock) {
      closing = true;
      wakeupClock();
    }
    call(() -> thread.join());
    LOG.info("Closed {}.", this);
  }
}
