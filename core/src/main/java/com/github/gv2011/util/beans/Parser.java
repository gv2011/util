package com.github.gv2011.util.beans;

@FunctionalInterface
public interface Parser<T> {

  T parse(final String encoded, ExtendedBeanBuilder<T> builder);

}
