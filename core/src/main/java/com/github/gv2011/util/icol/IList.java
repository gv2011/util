package com.github.gv2011.util.icol;

/*-
 * %---license-start---
 * The MIT License (MIT)
 * %
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %
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
 * %---license-end---
 */



import static com.github.gv2011.util.CollectionUtils.iCollections;

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
  default <T> T[] toArray(final T[] a) {
    throw new UnsupportedOperationException();
  }

  default IList<E> tail(){
    return subList(1, size());
  }

  default IList<E> append(final E element){
    return iCollections().<E>listBuilder().addAll(this).add(element).build();
  }

}
