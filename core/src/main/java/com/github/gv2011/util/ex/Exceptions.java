package com.github.gv2011.util.ex;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */




import static org.slf4j.LoggerFactory.getLogger;

import java.io.InterruptedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;

import com.github.gv2011.util.OptCloseable;
import com.github.gv2011.util.ann.Nullable;

public final class Exceptions {

  private Exceptions(){staticClass();}

  public static final boolean ASSERTIONS_ON;

  private final static Logger LOG = getLogger(Exceptions.class);

  static{
    boolean on = false;
    assert on=true;
    ASSERTIONS_ON = on;
  }

  public static RuntimeException notYetImplementedException(){
    return new NotYetImplementedException();
  }

  public static RuntimeException notYetImplementedException(final String msg){
    return new NotYetImplementedException(msg);
  }

  public static <T> T notYetImplemented(){
    throw notYetImplementedException();
  }

  public static RuntimeException bug(){
    return new Bug();
  }

  public static RuntimeException bug(final Supplier<String> message){
    return new Bug(message.get());
  }

  public static RuntimeException wrap(final Throwable e){
    if(e instanceof RuntimeException) return (RuntimeException)e;
    else return map(e, e.getMessage());
  }

  public static RuntimeException wrap(final Exception e, final String msg){
    return map(e, msg);
  }

  private static RuntimeException map(final Throwable e, final String msg){
    if(e instanceof InterruptedException || e instanceof InterruptedIOException)
      return new InterruptedRtException(e, msg);
    else return new WrappedException(e, msg);
  }

  public static String format(final String pattern, final @Nullable Object... arguments) {
    return MessageFormatter.arrayFormat(pattern, arguments).getMessage();
  }

  public static <T> T call(final Callable<T> operation){
    try {
      return operation.call();
    }
    catch (final RuntimeException e) {
      throw e;
      }
    catch (final Exception e) {
      throw new WrappedException(e);
    }
  }

  public static void run(final ThrowingRunnable operation){
    try {
      operation.run();
    }
    catch (final RuntimeException e) {throw e;}
    catch (final Exception e) {throw new WrappedException(e);}
  }

  public static void tryAll(final Runnable... operations){
    tryAll(Arrays.asList(operations));
  }

  /**
   * Tries to do all the operations even if some throw exceptions.
   * All but the last exception will be logged, but otherwise ignored.
   */
  public static void tryAll(
    final List<Runnable> operations
  ){
    if(operations.isEmpty()); //do nothing
    else if(operations.size()==1) {
      operations.get(0).run();
    }
    else{
      Throwable t1 = null;
      try{
        try{operations.get(0).run();}
        catch(final Throwable t){
          t1 = t;
          throw t;
        }
      }finally{
        try{tryAll(operations.subList(1, operations.size()));}
        catch(final Throwable t2){
          if(t1!=null){
            //Log this exception, because it is hidden by t2.
            LOG.error(t1.getMessage(), t1);
          }
          throw t2;
        }
      }
    }
  }

  /**
   * Logs the exception if assertions are off, otherwise throws it.
   */
  public static final void tolerate(final Throwable t){
    if(ASSERTIONS_ON){
      throw wrap(t);
    }
    else LOG.error(t.getMessage()+" (tolerated)", t);
  }

  public static void staticClass(){
    throw new RuntimeException("This is a static class without instances.");
  }

  public static <C extends OptCloseable,R> R callWithOptCloseable(
      final ThrowingSupplier<C> supplier, final ThrowingFunction<C,R> function
    ){
      return callWithCloseable(supplier, function, OptCloseable::close);
    }

  public static <C extends AutoCloseable,R> R callWithCloseable(
      final ThrowingSupplier<C> supplier, final ThrowingFunction<C,R> function
    ){
      return callWithCloseable(supplier, function, AutoCloseable::close);
    }

  public static <C,R> R callWithCloseable(
    final ThrowingSupplier<C> supplier, final ThrowingFunction<C,R> function, final ThrowingConsumer<? super C> closer
  ){
    try{
      final C closeable = supplier.get();
      try{
        return function.apply(closeable);
      }finally{
        closer.accept(closeable);
      }
    }
    catch(final Exception ex){throw wrap(ex);}
  }

  public static <C extends OptCloseable> void doWithOptCloseable(
    final ThrowingSupplier<C> supplier, final ThrowingConsumer<C> consumer
  ){
    doWithCloseable(supplier, consumer, OptCloseable::close);
  }

  public static <C extends AutoCloseable> void doWithCloseable(
    final ThrowingSupplier<C> supplier, final ThrowingConsumer<C> consumer
  ){
    doWithCloseable(supplier, consumer, AutoCloseable::close);
  }

  public static <C> void doWithCloseable(
    final ThrowingSupplier<C> supplier, final ThrowingConsumer<C> consumer, final ThrowingConsumer<? super C> closer
  ){
    try{
      final C closeable = supplier.get();
      try{consumer.accept(closeable);}
      finally{closer.accept(closeable);}
    }
    catch(final Exception ex){throw wrap(ex);}
  }


}
