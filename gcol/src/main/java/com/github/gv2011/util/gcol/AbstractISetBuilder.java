package com.github.gv2011.util.gcol;

import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.format;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.gv2011.util.icol.CollectionBuilder;
import com.github.gv2011.util.icol.ICollection;
import com.github.gv2011.util.icol.ISet;
import com.google.common.collect.ImmutableList;

abstract class AbstractISetBuilder<S extends ISet<E>,E,B extends CollectionBuilder<S,E,B>>
implements CollectionBuilder<S,E,B> {

  protected final Set<E> set = Collections.synchronizedSet(new HashSet<>());

  protected abstract B self();

  @Override
  public final int size() {
    return set.size();
  }

  @Override
  public final B add(final E element) {
    final boolean added = set.add(notNull(element));
    if(!added) throw new IllegalArgumentException(format("Set already contains {}.", element));
    return self();
  }

  @Override
  public final boolean tryAdd(final E element) {
    return set.add(notNull(element));
  }

  @Override
  public final <F extends E> B addAll(final Collection<F> elements) {
    final ImmutableList<E> copy = ImmutableList.copyOf(elements);
    synchronized(set){
      verify(copy.stream().allMatch(e->e!=null && !set.contains(e)));
      set.addAll(copy);
    }
    return self();
  }

  @Override
  public final <F extends E> B addAll(final ICollection<F> elements) {
    synchronized(set){
      verify(elements.stream().allMatch(e->!set.contains(e)));
      set.addAll(elements);
    }
    return self();
  }



  @Override
  public final <F extends E> B tryAddAll(final ICollection<F> elements) {
    set.addAll(elements);
    return self();
  }

  @Override
  public final <F extends E> B tryAddAll(final Collection<F> elements) {
    final ImmutableList<E> copy = ImmutableList.copyOf(elements);
    verify(copy.stream().allMatch(e->e!=null));
    set.addAll(copy);
    return self();
  }

}
