package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

public final class ExecutorUtils {

  private ExecutorUtils(){staticClass();}

  private static final Logger LOG = getLogger(ExecutorUtils.class);

  public static AutoCloseableNt asCloseable(final ExecutorService es){
    return ()->{
      es.shutdown();
      boolean terminated = false;
      while(!terminated){
        terminated = call(()->es.awaitTermination(5, TimeUnit.SECONDS));
        if(!terminated) LOG.info("Waiting for termination of executor service.");
      }
      LOG.debug("Executor service terminated.");
    };
  }

  public static <T> CloseableFuture<T> callAsync(final Callable<T> task){
    return new CloseableFutureImp<T>(task);
  }

}
