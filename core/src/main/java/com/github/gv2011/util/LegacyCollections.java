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
}
