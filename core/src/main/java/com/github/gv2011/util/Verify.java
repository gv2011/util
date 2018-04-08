package com.github.gv2011.util;

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

  public static <T> T verify(final T arg, final Predicate<? super T> predicate, final Function<? super T,String> msg) {
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

  public static <T> UnaryOperator<T> check(final Predicate<T> predicate) {
    return check(predicate, (Function<T,String>)String::valueOf);
  }

//  TODO remove
//  @Deprecated //use check
//  public static <T> UnaryOperator<T> verify(final Predicate<T> predicate) {
//    return check(predicate);
//  }

  public static <T> UnaryOperator<T> check(final Predicate<T> predicate, final Function<? super T,String> msg) {
    return e->{return verify(e, predicate, msg);};
  }

  public static <T> T notNull(final T arg){
    return notNull(arg, ()->"Null value.");
  }

  public static <T> T notNull(final T arg, final Supplier<String> msg){
    return verify(arg, a->a!=null, a->msg.get());
  }

  public static Runnable noop(){return ()->{};}

  public static <T> Optional<T> tryCast(final Object obj, final Class<? extends T> clazz){
    if(clazz.isInstance(obj)) return Optional.of((T)clazz.cast(obj));
    else return Optional.empty();
  }

}
