package com.github.gv2011.util.icol;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %%
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
 * #L%
 */




import static com.github.gv2011.util.CollectionUtils.iCollections;
import static com.github.gv2011.util.CollectionUtils.toISortedMap;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public interface IList<E> extends List<E>, ICollection<E>{

  public static interface Builder<E> extends CollectionBuilder<IList<E>,E,Builder<E>>{}

  @SuppressWarnings("unchecked")
  static <E> IList<E> cast(final IList<? extends E> list){return (IList<E>) list;}

  @Override
  IList<E> subList(int fromIndex, int toIndex);

  default ISortedMap<Integer,E> asMap(){
    return IntStream.range(0, size()).parallel().mapToObj(Integer::valueOf)
      .collect(toISortedMap(
          i->i,
          this::get
      ))
    ;
  }

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

  default IList<E> tail(){
    return subList(1, size());
  }

  default IList<E> append(final E element){
    return iCollections().<E>listBuilder().addAll(this).add(element).build();
  }

  default IList<E> appendAll(final Iterable<? extends E> elements){
    final Builder<E> b = iCollections().<E>listBuilder();
    b.addAll(this);
    for(final E e: elements) b.add(e);
    return b.build();
  }

  @Override
  default IList<E> asList() {
    return this;
  }

  @Override
  default Object[] toArray() {
    final int size = size();
    final Object[] result = new Object[size];
    for(int i=0; i<size; i++) result[i]=get(i);
    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  default <T> T[] toArray(T[] a) {
    final int size = size();
    if(a.length<size){
      a = (T[]) Array.newInstance(a.getClass().getComponentType(),size);
    }
    for(int i=0; i<size; i++) a[i]=(T) get(i);
    if(a.length>size) a[size] = null;
    return a;
  }

}
