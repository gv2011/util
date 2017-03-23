package com.github.gv2011.util;

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
