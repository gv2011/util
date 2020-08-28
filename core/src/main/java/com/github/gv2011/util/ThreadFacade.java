package com.github.gv2011.util;


public interface ThreadFacade {

  boolean isAlive();

  void interrupt();

  void join();

}
