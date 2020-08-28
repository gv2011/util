package com.github.gv2011.util;


public interface AutoCloseableNt extends AutoCloseable, OptCloseable{

  @Override
  void close();

}
