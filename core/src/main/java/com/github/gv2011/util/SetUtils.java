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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.github.gv2011.util.icol.ISet;

public final class SetUtils {

  private SetUtils(){staticClass();}

  @SafeVarargs
  public static <E, F extends E> Set<E> asSet(final F... elements){
    final Set<E> result = new HashSet<>(elements.length);
    for(int i=0; i<elements.length; i++) result.add(elements[i]);
    return result;
  }

  @SafeVarargs
  public static <E, F extends E> ISet<E> asISet(final F... elements){
    return CollectionUtils.iCollections().asSet(elements);
  }

  public static <E> Set<E> intersection(final Collection<? extends E> set1, final Collection<? extends E> set2){
    final Set<E> result = new HashSet<>(set1);
    result.retainAll(set2);
    return result;
  }

  public static <E> Set<E> unique(final Iterable<? extends E> collection) {
    final Set<E> result = new HashSet<>();
    final Set<E> duplicates = new HashSet<>();
    for(final E e: collection){
      if(!duplicates.contains(e)){
        if(result.contains(e)){
          duplicates.add(e);
          result.remove(e);
        }else result.add(e);
      }
    }
    return result;
  }

}
