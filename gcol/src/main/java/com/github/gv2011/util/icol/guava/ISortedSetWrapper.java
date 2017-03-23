package com.github.gv2011.util.icol.guava;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Optional;

import com.github.gv2011.util.icol.ISortedSet;
import com.google.common.collect.ImmutableSortedSet;

final class ISortedSetWrapper<E> extends ISetWrapper<E,NavigableSet<E>> implements ISortedSet<E>{

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
  public Optional<E> tryGetlower(final E e) {
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
