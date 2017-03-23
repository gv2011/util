package com.github.gv2011.util.icol.guava;

import java.util.Comparator;

import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISet.Builder;
import com.github.gv2011.util.icol.ISortedSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

final class ISetBuilder<E> extends AbstractISetBuilder<ISet<E>,E,ISet.Builder<E>> implements ISet.Builder<E>{

  @Override
  protected Builder<E> self() {
    return this;
  }

  @Override
  public ISet<E> build() {
    synchronized(set){
      return new ISetWrapper<>(ImmutableSet.copyOf(set));
    }
  }

  @Override
  public ISortedSet<E> build(final Comparator<? super E> comparator) {
    synchronized(set){
      return new ISortedSetWrapper<>(ImmutableSortedSet.copyOf(comparator, set));
    }
  }

}
