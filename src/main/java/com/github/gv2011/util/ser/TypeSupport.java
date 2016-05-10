package com.github.gv2011.util.ser;

import java.util.Map;

public interface TypeSupport<A,E> {

  boolean isBean(final Object obj);

  Map<A,?> asBean(final Object obj);

  E asElementary(final Object obj);

  boolean isElementary(final Object obj);

  boolean isMap(final Object obj);

  Map<?,?> asMap(final Object obj);

  boolean isList(final Object obj);

  Iterable<?> asList(final Object obj);


}
