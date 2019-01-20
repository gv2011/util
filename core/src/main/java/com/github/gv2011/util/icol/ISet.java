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

import static com.github.gv2011.util.icol.ICollections.toISet;

import java.util.Collection;
import java.util.Set;

import com.github.gv2011.util.XStream;

public interface ISet<E> extends Set<E>, ICollectionG<E, ISet<E>>{

  public static interface Builder<E> extends CollectionBuilder<ISet<E>,E,Builder<E>>{}

  @SuppressWarnings("unchecked")
  static <E> ISet<E> cast(final ISet<? extends E> set){return (ISet<E>) set;}

  @Override
  default boolean isEmpty() {
    return size()==0;
  }

  @Override
  default Object[] toArray() {
    return asList().toArray();
  }

  @Override
  default ISet<E> subtract(final Collection<?> other) {
    if(other.isEmpty()) return this;
    else{
      return parallelStream().filter(e->!other.contains(e)).collect(toISet());
    }
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
  default XStream<E> stream() {
      return XStream.stream(spliterator(), false);
  }


  @Override
  default XStream<E> parallelStream() {
      return XStream.stream(spliterator(), true);
  }

  @Override
  default ISet<E> join(final Collection<? extends E> other) {
    return parallelStream().concat(other.parallelStream()).collect(toISet());
  }

}
