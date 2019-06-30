package com.github.gv2011.util.lock;

import com.github.gv2011.util.AutoCloseableNt;

public interface Latch<T> extends AutoCloseableNt{

  static <T> Latch<T> create(){
    return Lock.FACTORY.get().createLatch();
  }

  void release(T value);

  T await();


}
