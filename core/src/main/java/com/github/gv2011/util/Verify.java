package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.github.gv2011.util.ann.Nullable;

public final class Verify {

  private Verify(){staticClass();}

  public static void verify(final boolean expr) {
    if(!expr) throw new IllegalStateException();
  }
  public static <T> T verify(final T arg, final Predicate<? super T> predicate) {
    return verify(arg, predicate, a->format("Unexpected: {}", a));
  }

  public static <T> T verify(final T arg, final Predicate<? super T> predicate, final Function<T,String> msg) {
    if(!predicate.test(arg)){
      throw new IllegalStateException(msg.apply(arg));
    }
    return arg;
  }

  public static void verify(final boolean expr, final Supplier<String> msg) {
    if(!expr) throw new IllegalStateException(msg.get());
  }

  public static void verify(final boolean expr, final String pattern, final Object... params) {
    verify(expr, ()->format(pattern, params));
  }

  public static <T> T verifyEqual(final @Nullable T actual, final @Nullable T expected) {
    if(!Objects.equals(actual, expected)){
      throw new IllegalStateException(format("Expected: {}, actual: {}.", expected, actual));
    }
    return actual;
  }

  public static <T> T verifyEqual(final T actual, final T expected, final BiFunction<T,T,String> msg) {
    if(!actual.equals(expected)){
      throw new IllegalStateException(msg.apply(expected, actual));
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
    return verify(arg, a->a!=null, a->msg.get());
  }

  public static <T> Optional<T> nothing(){return Optional.empty();}

  public static Runnable noop(){return ()->{};}

  public static <T> Optional<T> tryCast(final Object obj, final Class<? extends T> clazz){
    if(clazz.isInstance(obj)) return Optional.of((T)clazz.cast(obj));
    else return Optional.empty();
  }

}
