package com.github.gv2011.util.ex;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.InterruptedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;

public final class Exceptions {

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

  public static String format(final String pattern, final Object... arguments) {
    return MessageFormatter.arrayFormat(pattern, arguments).getMessage();
  }

  public static <T> T call(final Callable<T> operation){
    try {
      return operation.call();
    }
    catch (final RuntimeException e) {throw e;}
    catch (final Exception e) {throw new WrappedException(e);}
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


}
