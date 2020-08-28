package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

public final class ExecutorUtils {

  private ExecutorUtils(){staticClass();}

  private static final Logger LOG = getLogger(ExecutorUtils.class);

  public static Executor createExcecutor(){
    return asCloseable(Executors.newCachedThreadPool());
  }

  public static Executor asCloseable(final ExecutorService es){
    return new ExecutorWrapper(es);
  }

  public static <T> CloseableFuture<T> callAsync(final Callable<T> task){
    return new CloseableFutureImp<>(task);
  }
  
  private static final class ExecutorWrapper implements Executor{
    private final ExecutorService delegate;

    private ExecutorWrapper(ExecutorService delegate) {
      this.delegate = delegate;
    }

    @Override
    public <T> CloseableFuture<T> submit(Callable<? extends T> task) {
      return new CFImp<>(delegate.submit(task));
    }

    @Override
    public void close() {
      delegate.shutdown();
      boolean terminated = false;
      while(!terminated){
        terminated = call(()->delegate.awaitTermination(5, TimeUnit.SECONDS));
        if(!terminated) LOG.info("Waiting for termination of executor service.");
      }
      LOG.debug("Executor service terminated.");
    }  
  }

  private static final class CFImp<T> implements CloseableFuture<T>{
    
    private final Future<? extends T> delegate;

    private CFImp(Future<? extends T> delegate) {
      this.delegate = delegate;
    }

    @Override
    public T get() {
      return call(()->delegate.get());
    }

    @Override
    public void close() {
      get();
    }
    
  }
}
