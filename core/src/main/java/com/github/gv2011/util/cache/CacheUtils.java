package com.github.gv2011.util.cache;

import java.util.function.Supplier;

public final class CacheUtils {

  public static <T> Supplier<T> cache(final Supplier<T> supplier){
    return new SoftRefCache<T>(supplier)::get;
  }

}
