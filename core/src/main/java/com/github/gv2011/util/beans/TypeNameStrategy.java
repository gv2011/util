package com.github.gv2011.util.beans;

@FunctionalInterface
public interface TypeNameStrategy {

  String typeName(Class<?> type);

}
