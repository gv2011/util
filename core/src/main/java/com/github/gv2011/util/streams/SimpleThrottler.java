package com.github.gv2011.util.streams;

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

import static com.github.gv2011.util.time.TimeUtils.toSeconds;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

import com.github.gv2011.util.StreamUtils;
import com.github.gv2011.util.lock.Lock;
import com.github.gv2011.util.time.Clock;

public final class SimpleThrottler implements StreamUtils.Throttler{

  private final Lock lock = Lock.create();
  private final Supplier<Float> throttle;
  private final Clock clock = Clock.get();
  private final Instant lastTime = clock.instant();

  public SimpleThrottler(final Supplier<Float> throttle) {
    this.throttle = throttle;
  }

  @Override
  public int maxReadCount(final int limit) {
    return lock.get(()->{
      int allowance = 0;
      while(allowance<1){
        final Instant now = clock.instant();
        final float timePassed = (float)toSeconds(Duration.between(lastTime, now));
        final float bytesPerSecond = throttle.get();
        allowance = (int) (timePassed * bytesPerSecond);
        if(allowance<1) clock.sleep(Duration.ofSeconds(1));
      }
      return Math.min(limit, allowance);
    });
  }

}
