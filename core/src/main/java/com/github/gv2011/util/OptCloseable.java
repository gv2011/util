package com.github.gv2011.util;

public interface OptCloseable {

  /**
   * It is not guaranteed that {@link #closed()} returns true after calling {@link #close()}.
   */
  default void close(){};

  default boolean closed(){return false;}

}
