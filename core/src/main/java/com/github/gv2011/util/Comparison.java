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

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

public final class Comparison {

  private Comparison(){staticClass();}
  
  public static <C extends Comparable<C>> C min(final C c1, final C c2){
    final int diff = c1.compareTo(c2);
    assert (diff==0)==(c1.equals(c2));
    return diff<=0?c1:c2;
  }

  public static <C extends Comparable<C>> C max(final C c1, final C c2){
    final int diff = c1.compareTo(c2);
    assert (diff==0)==(c1.equals(c2));
    return diff>=0?c1:c2;
  }

  public static <C extends Iterable<? extends E>, E extends Comparable<? super E>> Comparator<C>
  listComparator(){
    return listComparator(Comparator.naturalOrder());
  }

  public static <C extends Iterable<? extends E>, E> Comparator<C>
  listComparator(final Comparator<? super E> comparator){
    return (c1,c2)->{
      final Iterator<? extends E> it1 = c1.iterator();
      final Iterator<? extends E> it2 = c2.iterator();
      int result = -2;
      while(result == -2){
        final boolean hasNext1 = it1.hasNext();
        final boolean hasNext2 = it2.hasNext();
        if(!hasNext1 || !hasNext2){
          result = hasNext1==hasNext2 ? 0 : hasNext1 ? 1:-1;
        }else{
          final E next1 = it1.next();
          final E next2 = it2.next();
          final int diff = comparator.compare(next1, next2);
          if(diff!=0) result = diff;
        }
      }
      return result;
    };
  }

  public static <S extends Set<? extends E>, E extends Comparable<? super E>> Comparator<S> setComparator(){
    return setComparator(Comparator.naturalOrder());
  }

  public static <S extends Set<? extends E>, E> Comparator<S> setComparator(final Comparator<? super E> comparator){
    return (s1,s2)->{
      int result = 0;
      if(s1!=s2){
        final TreeSet<E> all = new TreeSet<>(comparator);
        all.addAll(s1);
        all.addAll(s2);
        final Iterator<E> it = all.descendingIterator();
        while(result==0 && it.hasNext()){
          final E e = it.next();
          if(!s1.contains(e)){
            assert s2.contains(e);
            result = -1;
          }else if(!s2.contains(e)){
            result = 1;
          }
        }
      }
      return result;
    };
  }

  public static <E extends Comparable<? super E>> Comparator<Optional<E>> optComparator(){
    return (o1,o2)->{
      if(o1.isPresent()!=o2.isPresent()){
        return o1.isPresent() ? 1 : -1;
      }else if(!o1.isPresent()){
        assert !o2.isPresent();
        return 0;
      }else{
        final E e1 = o1.get();
        final E e2 = o2.get();
        if(e1==e2) return 0;
        else return e1.compareTo(e2);
      }
    };
  }

  public static <E,F extends Comparable<? super F>> Comparator<E> compareByAttribute(final Function<E,F> attribute){
    return (o1,o2)->attribute.apply(o1).compareTo(attribute.apply(o2));
  }



}
