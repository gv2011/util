package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.run;

import java.time.Duration;
import java.time.Instant;

class DefaultClock implements Clock{

  static final Clock INSTANCE = new DefaultClock();

  @Override
  public void await(final Instant instant) {
    final Duration sleepTime = Duration.between(instant(), instant);
    if(!(sleepTime.isNegative()||sleepTime.isZero())){
      run(()->Thread.sleep(sleepTime.toMillis()));
    }
  }

  @Override
  public Instant instant() {
    return Instant.now();
  }
}
