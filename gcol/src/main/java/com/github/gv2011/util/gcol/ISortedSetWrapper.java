package com.github.gv2011.util.gcol;

import static com.github.gv2011.util.Constants.softRefConstant;

import java.util.Collection;
import java.util.Iterator;
import java.util.NavigableSet;

import com.github.gv2011.util.CachedConstant;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.icol.Opt;
import com.google.common.collect.ImmutableSortedSet;

final class ISortedSetWrapper<E extends Comparable<? super E>>
extends ISetWrapper<E,NavigableSet<E>>
implements ISortedSet<E>{

  @SuppressWarnings({ "unchecked", "rawtypes" })
  static final ISortedSetWrapper EMPTY = new ISortedSetWrapper(ImmutableSortedSet.of());


  private final CachedConstant<IList<E>> index = softRefConstant(()->GuavaIcolFactory.INSTANCE.listFrom(this));

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
  public Opt<E> tryGetLast() {
    return isEmpty()?Opt.empty():Opt.of(delegate.last());
  }

  @Override
  public Opt<E> tryGetLower(final E e) {
    return Opt.ofNullable(delegate.lower(e));
  }

  @Override
  public Opt<E> tryGetFloor(final E e) {
    return Opt.ofNullable(delegate.floor(e));
  }

  @Override
  public Opt<E> tryGetCeiling(final E e) {
    return Opt.ofNullable(delegate.ceiling(e));
  }

  @Override
  public Opt<E> tryGetHigher(final E e) {
    return Opt.ofNullable(delegate.higher(e));
  }

  @Override
  public IList<E> subList(final int fromIndex, final int toIndex) {
    return index.get().subList(fromIndex, toIndex);
  }

  @Override
  public ISortedMap<Integer, E> asMap() {
    return index.get().asMap();
  }

  @Override
  public XStream<E> stream() {
    return GuavaIcolFactory.INSTANCE.xStream(delegate.stream());
  }

  @Override
  public XStream<E> parallelStream() {
      return GuavaIcolFactory.INSTANCE.xStream(delegate.parallelStream());
  }

  @Override
  public XStream<E> descendingStream() {
    return GuavaIcolFactory.INSTANCE.xStream(delegate.descendingSet().stream());
  }

  @Override
  public XStream<E> descendingStream(final E startExclusive) {
    return GuavaIcolFactory.INSTANCE.xStream(delegate.descendingSet().tailSet(startExclusive, false).stream());
  }

  @Override
  public ISortedSet<E> intersection(final Collection<?> other) {
    return intersection(this, other);
  }

  static <C extends Comparable<? super C>> ISortedSet<C> intersection(
    final ISortedSet<C> first, final Collection<?> second
  ) {
    return first.parallelStream().filter(second::contains).collect(GuavaIcolFactory.INSTANCE.sortedSetCollector());
  }

}
