package com.github.gv2011.util.streams;

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
