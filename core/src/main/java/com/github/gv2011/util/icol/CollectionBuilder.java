package com.github.gv2011.util.icol;

import java.util.Arrays;
import java.util.Collection;

import com.github.gv2011.util.Builder;

public interface CollectionBuilder<C extends ICollection<E>,E,B extends CollectionBuilder<C,E,B>> extends Builder<C>{

  B add(E element);

  /**
   * @return true, if added
   */
  boolean tryAdd(E element);

  default <F extends E> B addAll(final ICollection<F> elements){return addAll((Collection<F>) elements);}

  <F extends E> B addAll(Collection<F> elements);

  default <F extends E> B addAll(final F[] elements){return addAll(Arrays.asList(elements));}

  default <F extends E> B tryAddAll(final ICollection<F> elements){return tryAddAll((Collection<F>) elements);}

  <F extends E> B tryAddAll(Collection<F> elements);

}
