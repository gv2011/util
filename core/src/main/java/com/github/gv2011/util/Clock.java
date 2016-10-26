package com.github.gv2011.util;

import java.time.Instant;
import java.util.function.Supplier;

public interface Clock {

  public static final Supplier<Clock> INSTANCE = ()->DefaultClock.INSTANCE;

  Instant instant();

  void await(Instant nextRun);

}
