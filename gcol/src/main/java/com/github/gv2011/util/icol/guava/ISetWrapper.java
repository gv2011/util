package com.github.gv2011.util.icol.guava;

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
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.github.gv2011.util.icol.ISet;

class ISetWrapper<E,S extends Set<E>> implements ISet<E>{

  protected final S delegate;

  ISetWrapper(final S delegate) {
    this.delegate = delegate;
  }

  @Override
  public final void forEach(final Consumer<? super E> action) {
    delegate.forEach(action);
  }

  @Override
  public final int size() {
    return delegate.size();
  }

  @Override
  public final boolean isEmpty() {
    return delegate.isEmpty();
  }

  @Override
  public final boolean contains(final Object o) {
    return delegate.contains(o);
  }

  @Override
  public final Iterator<E> iterator() {
    return delegate.iterator();
  }

  @Override
  public final Object[] toArray() {
    return delegate.toArray();
  }

  @Override
  public final <T> T[] toArray(final T[] a) {
    return delegate.toArray(a);
  }

  @Override
  public final boolean containsAll(final Collection<?> c) {
    //TODO review
    return c.stream().allMatch(this::contains);
  }

  @Override
  public final boolean equals(final Object o) {
    return delegate.equals(o);
  }

  @Override
  public final int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public final Spliterator<E> spliterator() {
    return delegate.spliterator();
  }

  @Override
  public final Stream<E> stream() {
    return delegate.stream();
  }

  @Override
  public final Stream<E> parallelStream() {
    return delegate.parallelStream();
  }

  @Override
  public final String toString() {
    return delegate.toString();
  }



}
