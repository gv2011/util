package com.github.gv2011.util;

/**
 * The value of this constant is cached forever. If it is created with an supplier, the supplier is used at most
 * once.
 * If it was created without a supplier, the set method must be called before the get method.
 * The set method may be called many times, but the value must be always the same. If the set method is used after
 * the get method in the presence of a supplier, the value must be the same as that of the supplier.
 */
public interface CachedConstant<T> extends Constant<T>{

  /**
   * Must be called before {@link Constant#get}.
   */
  void set(T value);

}
