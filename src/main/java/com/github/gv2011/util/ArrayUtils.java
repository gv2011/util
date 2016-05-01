package com.github.gv2011.util;

import java.lang.reflect.Array;
import java.util.List;

public class ArrayUtils {

@SuppressWarnings("unchecked")
public static final <E> E[] toArray(final List<E> list, final E[] arrayTypeRef){
  return list.toArray((E[])Array.newInstance(arrayTypeRef.getClass(), list.size()));
}

}
