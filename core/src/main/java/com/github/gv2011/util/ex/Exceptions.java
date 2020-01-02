package com.github.gv2011.util.ex;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2019 Vinz (https://github.com/gv2011)
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
import java.io.InterruptedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import com.github.gv2011.util.Nothing;
import com.github.gv2011.util.OptCloseable;
import com.github.gv2011.util.ann.Nullable;

public final class Exceptions {

  private Exceptions(){staticClass();}

  public static final boolean ASSERTIONS_ON;

  private static @Nullable Logger logger = null;

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

  public static <T> T notYetImplemented(final String msg){
    throw notYetImplementedException(msg);
  }

  public static RuntimeException bug(){
    return new Bug();
  }

  public static <T> T bugValue(){
    throw bug();
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

  public static Runnable logExceptions(final Runnable r){
    return ()->{
      try {
        r.run();
      }
      catch (final Throwable t) {
        getLogger().error(format("Exception in {}.", Thread.currentThread()), t);
      }
    };
  }

  public static String format(final String pattern, final @Nullable Object... arguments) {
    return MessageFormatter.arrayFormat(pattern, arguments).getMessage();
  }

  public static <R> R call(final ThrowingSupplier<R> throwing, final Supplier<?> message){
    return throwing.asFunction().apply(null);
  }

  public static <R> R call(final ThrowingSupplier<R> throwing){
    return throwing.asFunction().apply(null);
  }

  public static Nothing call(final ThrowingRunnable throwing){
    return throwing.asFunction().apply(null);
  }

  public static void tryAll(final ThrowingRunnable... operations){
    tryAll(Arrays.asList(operations));
  }

  /**
   * Tries to do all the operations even if some throw exceptions.
   * All but the last exception will be logged, but otherwise ignored.
   */
  public static void tryAll(
    final List<ThrowingRunnable> operations
  ){
    if(operations.isEmpty()); //do nothing
    else if(operations.size()==1) {
      call(()->operations.get(0).run());
    }
    else{
      Throwable t1 = null;
      try{
        try{operations.get(0).run();}
        catch(final Throwable t){
          t1 = t;
          throw wrap(t);
        }
      }finally{
        try{tryAll(operations.subList(1, operations.size()));}
        catch(final Throwable t2){
          if(t1!=null){
            //Log this exception, because it is hidden by t2.
            getLogger().error(t1.getMessage(), t1);
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
    else getLogger().error(t.getMessage()+" (tolerated)", t);
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
    final ThrowingSupplier<C> supplier,
    final ThrowingFunction<C,R> function,
    final ThrowingConsumer<? super C> closer
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

  public static <C extends AutoCloseable, I extends AutoCloseable, R> R callWithCloseable(
    final ThrowingSupplier<C> supplier,
    final ThrowingFunction<C,I> wrapper,
    final ThrowingFunction<I,R> function
  ){
    @Nullable I wrapped = null;
    try{
      final C closeable = supplier.get();
      try{
        wrapped = wrapper.apply(closeable);
        return function.apply(wrapped);
      }finally{
        if(wrapped == null) closeable.close();
        else wrapped.close();
      }
    }
    catch(final Exception ex){throw wrap(ex);}
  }

  public static <C extends AutoCloseable> Nothing callWithCloseable(
    final ThrowingSupplier<C> supplier, final ThrowingConsumer<C> consumer
  ){
    callWithCloseable(supplier, consumer, AutoCloseable::close);
    return Nothing.INSTANCE;
  }

  public static <C extends OptCloseable> Nothing callWithOptCloseable(
    final ThrowingSupplier<C> supplier, final ThrowingConsumer<C> consumer
  ){
    return callWithCloseable(supplier, consumer, OptCloseable::close);
  }

  public static <C> Nothing callWithCloseable(
    final ThrowingSupplier<C> supplier,
    final ThrowingConsumer<C> consumer,
    final ThrowingConsumer<? super C> closer
  ){
    try{
      final C closeable = supplier.get();
      try{consumer.accept(closeable);}
      finally{closer.accept(closeable);}
    }
    catch(final Exception ex){throw wrap(ex);}
    return Nothing.INSTANCE;
  }

  public static <CI extends AutoCloseable, CO extends AutoCloseable> CO wrapCloseable(
    final CI inner, final ThrowingFunction<CI, CO> wrapping
  ) {
    boolean success = false;
    try {
      final CO result = wrapping.apply(inner);
      success = false;
      return result;
    } catch (final Exception e) {
      throw wrap(e);
    } finally {
      if(!success) call(inner::close);
    }
  }

	  /**
   * Lazy creation because of bootstrapping.
   */
  private static Logger getLogger() {
    Logger logger = Exceptions.logger;
    if(logger==null){
      logger = LoggerFactory.getLogger(Exceptions.class);
      Exceptions.logger = logger;
    }
    return logger;
  }


}
