package com.github.gv2011.util.cache;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.util.function.Supplier;

public final class CacheUtils {

  private CacheUtils(){staticClass();}

  public static <T> Supplier<T> cache(final Supplier<T> supplier){
    return new SoftRefCache<>(supplier)::get;
  }

}
