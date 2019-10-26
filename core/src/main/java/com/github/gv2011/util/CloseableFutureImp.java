package com.github.gv2011.util;

import static com.github.gv2011.util.Nothing.nothing;
import static com.github.gv2011.util.ex.Exceptions.call;

import java.util.concurrent.Callable;

import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.lock.Lock;

final class CloseableFutureImp<T> implements CloseableFuture<T>{

  private final Lock lock = Lock.FACTORY.get().create();
  private final Thread thread;

  private Opt<T> result = Opt.empty();
  private Opt<Throwable> exception = Opt.empty();

  public CloseableFutureImp(final Callable<T> task) {
    thread = new Thread(()->{
      try {
        final T result = task.call();
        lock.run(()->{this.result = Opt.of(result);}, true);
      }
      catch (final Throwable t) {
        lock.run(()->{this.exception = Opt.of(t);}, true);
      }
    });
    thread.start();
  }

  @Override
  public T get() {
    return lock.apply(nothing(), n->{
      Opt<Throwable> exception = this.exception;
      Opt<T> result = this.result;
      while(result.isEmpty() && exception.isEmpty()){
        lock.await();
        exception = this.exception;
        result = this.result;
      }
      if(exception.isPresent()) throw new RuntimeException("Exception in async task.", exception.get());
      else return result.get();
    });
  }

  @Override
  public void close() {
    get();
    call(()->thread.join());
  }

}
