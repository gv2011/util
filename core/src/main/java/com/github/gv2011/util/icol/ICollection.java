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




import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;

import com.github.gv2011.util.XStream;

public interface ICollection<E> extends Collection<E>{

  default Optional<E> asOptional(){
    final int size = size();
    if(size==0) return Optional.empty();
    else if(size==1) return Optional.of(iterator().next());
    else throw new IllegalStateException();
  }

  default E single(){
    return asOptional().get();
  }

  default E first(){
    if(isEmpty()) throw new NoSuchElementException();
    else return iterator().next();
  }

  default Optional<E> tryGetFirst(){
    if(isEmpty()) return Optional.empty();
    else return Optional.of(iterator().next());
  }



  @Override
  @Deprecated
  default boolean add(final E e) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default boolean addAll(final Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default boolean remove(final Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default boolean removeAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default boolean removeIf(final Predicate<? super E> filter) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default boolean retainAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default <T> T[] toArray(final T[] a) {
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
}
