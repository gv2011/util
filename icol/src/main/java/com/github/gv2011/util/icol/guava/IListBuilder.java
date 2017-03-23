package com.github.gv2011.util.icol.guava;

import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.github.gv2011.util.icol.ICollection;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IList.Builder;
import com.google.common.collect.ImmutableList;

final class IListBuilder<E> implements IList.Builder<E> {

  private final List<E> list = Collections.synchronizedList(new ArrayList<>());

  @Override
  public Builder<E> add(final E element) {
    list.add(notNull(element));
    return this;
  }

  @Override
  public Builder<E> tryAdd(final E element) {
    list.add(notNull(element));
    return this;
  }

  @Override
  public <F extends E> Builder<E> addAll(final Collection<F> elements) {
    verify(elements.stream().allMatch(e->e!=null));
    list.addAll(elements);
    return this;
  }

  @Override
  public <F extends E> Builder<E> addAll(final ICollection<F> elements) {
    list.addAll(elements);
    return this;
  }

  @Override
  public <F extends E> Builder<E> tryAddAll(final ICollection<F> elements) {
    return addAll(elements);
  }

  @Override
  public <F extends E> Builder<E> tryAddAll(final Collection<F> elements) {
    return addAll(elements);
  }

  @Override
  public IList<E> build() {
    synchronized(list){
      return new IListWrapper<>(ImmutableList.copyOf(list));
    }
  }

}
