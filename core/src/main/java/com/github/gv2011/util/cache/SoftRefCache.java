package com.github.gv2011.util.cache;

import java.lang.ref.SoftReference;
import java.util.function.Supplier;

class SoftRefCache<T> {

  private final Supplier<T> constantFunction;
  private SoftReference<T> ref = new SoftReference<>(null);

  SoftRefCache(final Supplier<T> constantFunction) {
    this.constantFunction = constantFunction;
  }

  T get(){
    T result = ref.get();
    if(result==null){
      result = constantFunction.get();
      ref = new SoftReference<T>(result);
    }
    return result;
  }

}
