package com.github.gv2011.util.tstr;

public interface TypedString<T extends TypedString<T>>
extends Comparable<TypedString<?>>{

  T self();

  Class<T> clazz();

  default String canonical() {
    return toString();
  }
}
