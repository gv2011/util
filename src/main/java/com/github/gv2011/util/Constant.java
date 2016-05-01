package com.github.gv2011.util;

import java.util.function.Supplier;

import net.jcip.annotations.Immutable;

@Immutable
@FunctionalInterface
public interface Constant<T> extends Supplier<T>{

  /**
   * @return always the same value
   */
  @Override
  T get();

}
