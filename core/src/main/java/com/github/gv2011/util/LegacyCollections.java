package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.util.Enumeration;
import java.util.Iterator;

public final class LegacyCollections {

  private LegacyCollections(){staticClass();}

  public static <T> Iterator<T> asIterator(final Enumeration<? extends T> en){
    return new Iterator<T>(){
      @Override
      public boolean hasNext() {return en.hasMoreElements();}
      @Override
      public T next() {return en.nextElement();}
    };
  }

  public static <T> Enumeration<T> asEnumeration(final Iterator<? extends T> it){
    return new Enumeration<T>(){
      @Override
      public boolean hasMoreElements() {
        return it.hasNext();
      }
      @Override
      public T nextElement() {
        return it.next();
      }
    };
  }
}