package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.run;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.time.Duration;
import java.time.Instant;

public class TimeUtils {

  private TimeUtils(){staticClass();}

  public static void await(final Instant instant){
    final Duration time = Duration.between(Instant.now(), instant);
    if(!time.isNegative()) run(()->Thread.sleep(time.toMillis()));
  }
}
