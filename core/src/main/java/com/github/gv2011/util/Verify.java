package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.format;

import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Verify {

  public static void verify(final boolean expr) {
    if(!expr) throw new IllegalStateException();
  }
  public static <T> T verify(final T arg, final Predicate<? super T> predicate) {
    return verify(arg, predicate, ()->"");
  }

  public static <T> T verify(final T arg, final Predicate<? super T> predicate, final Supplier<String> msg) {
    if(!predicate.test(arg)) throw new IllegalStateException(msg.get());
    return arg;
  }

  public static void verify(final boolean expr, final Supplier<String> msg) {
    if(!expr) throw new IllegalStateException(msg.get());
  }

  public static void verify(final boolean expr, final String pattern, final Object... params) {
    verify(expr, ()->format(pattern, params));
  }

  public static <T> T notNull(final T arg){
    return notNull(arg, ()->"Null value.");
  }

  public static <T> T notNull(final T arg, final Supplier<String> msg){
    return verify(arg, a->a!=null, ()->"Null value.");
  }

}