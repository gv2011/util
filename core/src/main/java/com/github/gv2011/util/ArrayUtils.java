package com.github.gv2011.util;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

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
