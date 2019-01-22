package com.github.gv2011.util.time;

import static com.github.gv2011.util.CollectionUtils.tryGetFirstKey;
import static com.github.gv2011.util.Verify.notNull;
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
import static com.github.gv2011.util.icol.ICollections.emptyList;
import static com.github.gv2011.util.icol.ICollections.toIList;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gv2011.util.AutoCloseableNt;
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
        call(()->awaitLock.wait());
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
    notNull(obj);
    if (!notifyAt.isAfter(now)) {
      synchronized (obj) {
        obj.notifyAll();
      }
    } else {
      synchronized (lock) {
        final Opt<Instant> actualNext = tryGetFirstKey(notifications);
        final List<Object> list = notifications.computeIfAbsent(notifyAt, t -> new ArrayList<>());
        list.add(obj);
        LOG.debug("Registered object for notification at {} (count: {}).", notifyAt, list.size());
        if (actualNext.map(n -> notifyAt.isBefore(n)).orElse(true)) {
          if(LOG.isDebugEnabled()) {LOG.debug(
            "Next notification time changed from {} to {}.", toString(actualNext), notifyAt
          );}
          wakeupClock();
        }
      }
    }
  }

  private void wakeupClock() {
    assert Thread.holdsLock(lock);
    LOG.debug("Interrupting {}.", thread);
    thread.interrupt();
  }

  private void run() {
    boolean shouldRun = true;
    while (shouldRun) {
      final Instant now = instant();
      final Opt<Instant> sleepUntil; //empty means unlimited
      final List<Object> objectsToNotify;
      synchronized (lock) {
        if (closing) {
          shouldRun = false;
          objectsToNotify = notifications.values().parallelStream().flatMap(List::parallelStream).collect(toIList());
          notifications.clear();
          LOG.trace("Closing, selected all ({} items) for notification.", objectsToNotify.size());
          sleepUntil = Opt.of(now);
        } else {
          final Opt<Instant> nextTime = tryGetFirstKey(notifications);
          if(nextTime.isPresent()){
            final Instant i = nextTime.get();
            if(now.isBefore(i)){
              objectsToNotify = emptyList();
              sleepUntil = Opt.of(i);
            }else{
              objectsToNotify = notifications.remove(i).parallelStream().collect(toIList());
              LOG.trace("Selected {} objects for notification at {}.", objectsToNotify.size(), i);
              sleepUntil = Opt.of(now);
            }
          }
          else{
            objectsToNotify = emptyList();
            sleepUntil = Opt.empty();
          }
        }
      }
      if(!objectsToNotify.isEmpty()){
        LOG.debug("Notifying {} objects.", objectsToNotify.size());
        objectsToNotify.parallelStream().forEach(this::notify);
      }
      sleepUntilInternal(now, sleepUntil);
    }
  }

  private void notify(final Object object){
    verify(!Thread.holdsLock(lock));
    synchronized(object){object.notifyAll();}
  }

  private void sleepUntilInternal(final Instant now, final Opt<Instant> t) {
    assert !Thread.holdsLock(lock);

    final Duration duration;
    boolean tickOnly;
    {
      if(t.isPresent()){
        final Duration fullDuration = Duration.between(now, t.get());
        if(fullDuration.compareTo(LOG_TICK_PERIOD)>0){
          tickOnly = true;
          duration = LOG_TICK_PERIOD;
        }
        else{
          tickOnly = false;
          duration = fullDuration;
        }
      }
      else{
        tickOnly = true;
        duration = LOG_TICK_PERIOD;
      }
    }

    if(greaterThanZero(duration)) {
      try {
        logSleep(t, duration, tickOnly);
        Thread.sleep(duration.toMillis());
      } catch (final InterruptedException e) {
        Thread.interrupted(); // clear interrupted status
        LOG.debug("Sleep interrupted.", duration);
      }
    }
  }

  private static void logSleep(final Opt<Instant> t, final Duration duration, final boolean tickOnly) {
    if(tickOnly) {
      if(LOG.isTraceEnabled()){
        LOG.trace("Going to sleep for {} (next tick). Next notification: {}.", duration, toString(t));
      }
    }
    else{
      if(LOG.isDebugEnabled()){
        LOG.debug("Going to sleep for {}. Next notification: {}.", duration, toString(t));
      }
    }
  }

  private static String toString(final Opt<Instant> optTime){
    return optTime.map(Instant::toString).orElse("-none-");
  }

  @Override
  public void close() {
    LOG.debug("Closing.");
    synchronized (lock) {
      closing = true;
      wakeupClock();
    }
    LOG.debug("Waiting for {}.", thread);
    call(() -> thread.join());
    LOG.info("Closed {}.", this);
  }

  private static boolean greaterThanZero(final Duration duration){
    return !duration.isNegative() && !duration.isZero();
  }
}
