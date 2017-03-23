package com.github.gv2011.util.icol;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;

final class EmptyISet<E> extends AbstractSet<E> implements ISortedSet<E>{

  @SuppressWarnings("rawtypes")
  private static final ISortedSet INSTANCE = new EmptyISet();

  @SuppressWarnings("unchecked")
  static <T> ISortedSet<T> empty() {
    return INSTANCE;
  }

  private EmptyISet(){}

  @Override
  public final int size() {
    return 0;
  }

  @Override
  public final boolean isEmpty() {
    return true;
  }

  @Override
  public final boolean contains(final Object o) {
    return false;
  }

  @Override
  public final int hashCode() {
    return 1;
  }

  @Override
  public final Stream<E> stream() {
    return Stream.empty();
  }

  @Override
  public final Stream<E> parallelStream() {
    return Stream.empty();
  }

  @Override
  public final Iterator<E> iterator() {
    return EmptyIterator.instance();
  }

  @Override
  public final boolean containsAll(final Collection<?> c) {
    return c.isEmpty();
  }


  @Override
  public final Optional<E> asOptional() {
    return Optional.empty();
  }


  @Override
  public final E single() {
    throw new NoSuchElementException();
  }


  @Override
  public final E first() {
    throw new NoSuchElementException();
  }


  @Override
  public final Optional<E> tryGetFirst() {
    return Optional.empty();
  }



  @Override
  public final E last() {
    throw new NoSuchElementException();
  }


  @Override
  public final ISortedSet<E> subSet(final E fromElement, final E toElement) {
    return this;
  }


  @Override
  public final ISortedSet<E> headSet(final E toElement) {
    return this;
  }


  @Override
  public final ISortedSet<E> tailSet(final E fromElement) {
    return this;
  }


  @Override
  public final Optional<E> tryGetLast() {
    return Optional.empty();
  }

  @Override
  public final Spliterator<E> spliterator() {
    return Spliterators.emptySpliterator();
  }

  @Override
  public ISortedSet<E> descendingSet() {
    return this;
  }

  @Override
  public Iterator<E> descendingIterator() {
    return EmptyIterator.instance();
  }

  @Override
  public ISortedSet<E> subSet(final E fromElement, final boolean fromInclusive, final E toElement, final boolean toInclusive) {
    return this;
  }

  @Override
  public ISortedSet<E> headSet(final E toElement, final boolean inclusive) {
    return this;
  }

  @Override
  public ISortedSet<E> tailSet(final E fromElement, final boolean inclusive) {
    return this;
  }

  @Override
  public Optional<E> tryGetlower(final E e) {
    return Optional.empty();
  }

  @Override
  public Optional<E> tryGetFloor(final E e) {
    return Optional.empty();
  }

  @Override
  public Optional<E> tryGetCeiling(final E e) {
    return Optional.empty();
  }

  @Override
  public Optional<E> tryGetHigher(final E e) {
    return Optional.empty();
  }


}
