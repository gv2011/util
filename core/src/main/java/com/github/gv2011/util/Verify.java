package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.format;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class Verify {

  public static void verify(final boolean expr) {
    if(!expr) throw new IllegalStateException();
  }
  public static <T> T verify(final T arg, final Predicate<? super T> predicate) {
    return verify(arg, predicate, ()->"");
  }

  public static <T> T verify(final T arg, final Predicate<? super T> predicate, final Supplier<String> msg) {
    if(!predicate.test(arg)){
      throw new IllegalStateException(msg.get());
    }
    return arg;
  }

  public static void verify(final boolean expr, final Supplier<String> msg) {
    if(!expr) throw new IllegalStateException(msg.get());
  }

  public static void verify(final boolean expr, final String pattern, final Object... params) {
    verify(expr, ()->format(pattern, params));
  }

  public static <T> T verifyEqual(final T actual, final T expected) {
    return verifyEqual(actual, expected, ()->format("Expected: {}, actual: {}.", expected, actual));
  }

  public static <T> T verifyEqual(final T actual, final T expected, final Supplier<String> msg) {
    if(!actual.equals(expected)){
      throw new IllegalStateException(msg.get());
    }
    return actual;
  }

  public static <T> UnaryOperator<T> verify(final Predicate<T> predicate) {
    return e->{return verify(e, predicate);};
  }

  public static <T> T notNull(final T arg){
    return notNull(arg, ()->"Null value.");
  }

  public static <T> T notNull(final T arg, final Supplier<String> msg){
    return verify(arg, a->a!=null, msg);
  }

  public static <T> Optional<T> nothing(){return Optional.empty();}

  public static Runnable noop(){return ()->{};}

}
