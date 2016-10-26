package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.lang.reflect.Array;
import java.util.List;

public final class ArrayUtils {

  private ArrayUtils(){staticClass();}

  @SuppressWarnings("unchecked")
  public static final <E> E[] toArray(final List<E> list, final E[] arrayTypeRef){
    return list.toArray((E[])Array.newInstance(arrayTypeRef.getClass(), list.size()));
  }

}
