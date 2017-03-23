package com.github.gv2011.util.icol;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.SortedSet;
import java.util.function.Function;

final class ISortedSetImp<E> extends AbstractISet<SortedSet<E>,E,E> implements ISortedSet<E>{

  static <T> ISortedSet<T> fromDelegate(final NavigableSet<T> delegate) {
    final int size = delegate.size();
    if(size>1) return new ISortedSetImp<>(delegate);
    else if(size==1) return new SingleISet<>(delegate.first());
    else return EmptyISet.empty();
  }

  private final NavigableSet<E> delegate;



  @Override
  protected final Function<E, E> mapping() {
    return Function.identity();
  }

  @Override
  public boolean contains(final Object o) {
    return delegate.contains(o);
  }

  ISortedSetImp(final NavigableSet<E> delegate) {
    this.delegate = delegate;
  }

  @Override
  protected SortedSet<E> delegate() {
    return delegate;
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
  public Optional<E> tryGetLast() {
    return Optional.of(delegate.last());
  }

  @Override
  public ISortedSet<E> subSet(final E fromElement, final E toElement) {
    return subSet(fromElement, true, toElement, false);
  }

  private ISortedSet<E> fromSubSet(final NavigableSet<E> subSet) {
    if(subSet.size()==size()) return this;
    else return fromDelegate(subSet);
  }

  @Override
  public ISortedSet<E> headSet(final E toElement) {
    return headSet(toElement, true);
  }

  @Override
  public ISortedSet<E> tailSet(final E fromElement) {
    return tailSet(fromElement, true);
  }

  @Override
  public ISortedSet<E> descendingSet() {
    return new ISortedSetImp<>(delegate.descendingSet());
  }

  @Override
  public Iterator<E> descendingIterator() {
    return new AbstractIterator<>(delegate.descendingIterator(), mapping());
  }

  @Override
  public ISortedSet<E> subSet(
    final E fromElement, final boolean fromInclusive, final E toElement, final boolean toInclusive
  ) {
    return fromSubSet(delegate.subSet(fromElement, fromInclusive, toElement, toInclusive));
  }

  @Override
  public ISortedSet<E> headSet(final E toElement, final boolean inclusive) {
    return fromSubSet(delegate.headSet(toElement, inclusive));
  }

  @Override
  public ISortedSet<E> tailSet(final E fromElement, final boolean inclusive) {
    return fromSubSet(delegate.tailSet(fromElement, inclusive));
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
