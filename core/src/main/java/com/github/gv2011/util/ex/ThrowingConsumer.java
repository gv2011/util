package com.github.gv2011.util.ex;

@FunctionalInterface
public interface ThrowingConsumer<T> {

  void accept(T arg) throws Exception;

  default ThrowingConsumer<T> andThen(final ThrowingConsumer<? super T> next) {
    return (final T arg) -> {accept(arg); next.accept(arg);};
  }

}
