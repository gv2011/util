package com.github.gv2011.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SetUtils {

  public static <E, F extends E> Set<E> asSet(final F[] array){
    final Set<E> result = new HashSet<>(array.length);
    for(int i=0; i<array.length; i++) result.add(array[i]);
    return result;
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
