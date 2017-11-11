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

import java.util.function.Function;

public final class Equal {

  private Equal(){staticClass();}

  public static final boolean equal(final Object o1, final Object o2){
    boolean result;
    if(o1==o2) result = true;
    else if(o1==null) result = false;
    else {
      result = o1.equals(o2);
      assert result==o2.equals(o1) : "Inconsistent equals implementation.";
    }
    return result;
  }

  public static <T> boolean equal(final T o1, final Object o2, final Class<T> clazz, final Function<T,Boolean> eq){
    if(o1==o2) return true;
    else if(!clazz.isInstance(o2)) return false;
    else return eq.apply(clazz.cast(o2));
  }

  public static <T extends Comparable<? super T>> boolean equal(final T o1, final Object o2, final Class<T> clazz){
    if(o1==o2) return true;
    else if(!clazz.isInstance(o2)) return false;
    else return o1.compareTo(clazz.cast(o2))==0;
  }

  public static int hashCode(final Class<?> clazz, final Object att1, final Object... more){
    int hash = (clazz.hashCode()*31)+att1.hashCode();
    for(final Object o: more) hash = hash*31 + o.hashCode();
    return hash;
  }

}
