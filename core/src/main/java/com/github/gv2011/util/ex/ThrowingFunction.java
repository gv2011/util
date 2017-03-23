package com.github.gv2011.util.ex;

@FunctionalInterface
public interface ThrowingFunction<T,R> {

  R apply(T argument) throws Exception;

}
