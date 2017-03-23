package com.github.gv2011.util.icol;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

public interface ISet<E> extends Set<E>, ICollection<E>{

  public static interface Builder<E> extends CollectionBuilder<ISet<E>,E,Builder<E>>{
    ISortedSet<E> build(Comparator<? super E> comparator);
  }

  @Override
  default boolean isEmpty() {
    return size()==0;
  }

  @Deprecated
  @Override
  default boolean add(final E e) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default boolean remove(final Object o) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default boolean addAll(final Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default boolean removeAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default boolean retainAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default <T> T[] toArray(final T[] a) {
    throw new UnsupportedOperationException();
  }

}
