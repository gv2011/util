package com.github.gv2011.util;

public interface LazyConstant<T> extends Constant<T>{

  /**
   * Must be called before {@link Constant#get}.
   */
  void set(T value);

}
