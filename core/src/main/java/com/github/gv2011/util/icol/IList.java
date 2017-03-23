package com.github.gv2011.util.icol;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.UnaryOperator;

public interface IList<E> extends List<E>, ICollection<E>{

  public static interface Builder<E> extends CollectionBuilder<IList<E>,E,Builder<E>>{}

  @Override
  IList<E> subList(int fromIndex, int toIndex);

  ISortedMap<Integer,E> asMap();

  @Deprecated
  @Override
  default boolean add(final E e) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default boolean addAll(final Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default boolean addAll(final int index, final Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default boolean retainAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default void replaceAll(final UnaryOperator<E> operator) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default void sort(final Comparator<? super E> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default void clear() {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default E set(final int index, final E element) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default void add(final int index, final E element) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default E remove(final int index) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default boolean removeAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default boolean remove(final Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default <T> T[] toArray(final T[] a) {
    throw new UnsupportedOperationException();
  }

}
