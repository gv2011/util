package com.github.gv2011.util;

import java.util.concurrent.Callable;

public interface Executor extends AutoCloseableNt{
  
  <T> CloseableFuture<T> submit(Callable<? extends T> task);

}
