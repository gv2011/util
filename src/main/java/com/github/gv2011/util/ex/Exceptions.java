package com.github.gv2011.util.ex;

import java.util.concurrent.Callable;

import org.slf4j.helpers.MessageFormatter;

public final class Exceptions {

  public static RuntimeException notYetImplementedException(){
    return new NotYetImplementedException();
  }

  public static RuntimeException bug(){
    return new Bug();
  }

  public static RuntimeException wrap(final Exception e){
    if(e instanceof RuntimeException) return (RuntimeException)e;
    else return new WrappedException(e);
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
}
