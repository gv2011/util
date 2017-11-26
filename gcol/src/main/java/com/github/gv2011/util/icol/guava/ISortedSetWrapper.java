package com.github.gv2011.util.icol.guava;

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

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Optional;

import com.github.gv2011.util.icol.ISortedSet;
import com.google.common.collect.ImmutableSortedSet;

final class ISortedSetWrapper<E extends Comparable<? super E>>
extends ISetWrapper<E,NavigableSet<E>>
implements ISortedSet<E>{

  ISortedSetWrapper(final NavigableSet<E> delegate) {
    super(delegate);
  }

  @Override
  public E lower(final E e) {
    return delegate.lower(e);
  }

  @Override
  public E floor(final E e) {
    return delegate.floor(e);
  }

  @Override
  public E ceiling(final E e) {
    return delegate.ceiling(e);
  }

  @Override
  public E higher(final E e) {
    return delegate.higher(e);
  }

  @Override
  public ISortedSet<E> descendingSet() {
    return new ISortedSetWrapper<>(delegate.descendingSet());
  }

  @Override
  public Iterator<E> descendingIterator() {
    return delegate.descendingIterator();
  }

  @Override
  public ISortedSet<E> subSet(
    final E fromElement, final boolean fromInclusive, final E toElement, final boolean toInclusive
  ) {
    return new ISortedSetWrapper<>(delegate.subSet(fromElement, fromInclusive, toElement, toInclusive));
  }

  @Override
  public ISortedSet<E> headSet(final E toElement, final boolean inclusive) {
    return new ISortedSetWrapper<>(delegate.headSet(toElement, inclusive));
  }

  @Override
  public E first() {
    return delegate.first();
  }

  @Override
  public E last() {
    return delegate.last();
  }

  @Override
  public ISortedSet<E> tailSet(final E fromElement, final boolean inclusive) {
    return new ISortedSetWrapper<>(delegate.tailSet(fromElement, inclusive));
  }

  @Override
  public ISortedSet<E> subSet(final E fromElement, final E toElement) {
    return new ISortedSetWrapper<>(ImmutableSortedSet.copyOf(delegate.subSet(fromElement, toElement)));
  }

  @Override
  public ISortedSet<E> headSet(final E toElement) {
    return new ISortedSetWrapper<>(ImmutableSortedSet.copyOf(delegate.headSet(toElement)));
  }

  @Override
  public ISortedSet<E> tailSet(final E fromElement) {
    return new ISortedSetWrapper<>(ImmutableSortedSet.copyOf(delegate.tailSet(fromElement)));
  }

  @Override
  public Optional<E> tryGetLast() {
    return isEmpty()?Optional.empty():Optional.of(delegate.last());
  }

  @Override
  public Optional<E> tryGetLower(final E e) {
    return Optional.ofNullable(delegate.lower(e));
  }

  @Override
  public Optional<E> tryGetFloor(final E e) {
    return Optional.ofNullable(delegate.floor(e));
  }

  @Override
  public Optional<E> tryGetCeiling(final E e) {
    return Optional.ofNullable(delegate.ceiling(e));
  }

  @Override
  public Optional<E> tryGetHigher(final E e) {
    return Optional.ofNullable(delegate.higher(e));
  }

}
