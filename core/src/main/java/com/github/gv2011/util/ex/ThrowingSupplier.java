package com.github.gv2011.util.ex;

@FunctionalInterface
public interface ThrowingSupplier<T> {

  T get() throws Exception;

}
