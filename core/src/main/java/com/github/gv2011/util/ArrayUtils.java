package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public final class ArrayUtils {

  private ArrayUtils(){staticClass();}

  @SuppressWarnings("unchecked")
  public static final <E> E[] toArray(final List<E> list, final E[] arrayTypeRef){
    return list.toArray((E[])Array.newInstance(arrayTypeRef.getClass(), list.size()));
  }

  public static <E,F extends E> Iterator<E> iterator(final F[] array){
    return new Iterator<E>(){
      private int i = 0;
      @Override
      public boolean hasNext() {
        return i<array.length;
      }
      @Override
      public E next() {
        if(!hasNext()) throw new NoSuchElementException();
        return array[i++];
      }
    };
  }
}
