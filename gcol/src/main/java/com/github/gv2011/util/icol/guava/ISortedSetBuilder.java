package com.github.gv2011.util.icol.guava;

import com.github.gv2011.util.icol.ISortedSet;
import com.google.common.collect.ImmutableSortedSet;

final class ISortedSetBuilder<E> extends AbstractISetBuilder<ISortedSet<E>,E,ISortedSet.Builder<E>>
implements ISortedSet.Builder<E>{

  @Override
  protected ISortedSetBuilder<E> self() {
    return this;
  }

  @Override
  public ISortedSet<E> build() {
    synchronized(set){
      return new ISortedSetWrapper<>(ImmutableSortedSet.copyOf(set));
    }
  }

}
