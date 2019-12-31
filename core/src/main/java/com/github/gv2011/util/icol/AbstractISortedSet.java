package com.github.gv2011.util.icol;

import static com.github.gv2011.util.icol.ICollections.emptySortedSet;
import static com.github.gv2011.util.icol.ICollections.toISortedSet;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;

public abstract class AbstractISortedSet<E extends Comparable<? super E>, S extends ISortedSet<E>>
implements ISortedSet<E>{

  @Override
  public int size() {
    return (int) parallelStream().count();
  }

  @Override
  public boolean contains(final Object o) {
    assert o!=null;
    return parallelStream().anyMatch(e->e.equals(o));
  }

  @Override
  public Iterator<E> iterator() {
    return stream().iterator();
  }

  @Override
  public Iterator<E> descendingIterator() {
    return descendingStream().iterator();
  }

  @Override
  public boolean containsAll(final Collection<?> c) {
    return c.parallelStream().allMatch(this::contains);
  }

  @Override
  public abstract S addElement(final E other);

  @Override
  public IList<E> subList(final int fromIndex, final int toIndex) {
    return asList().subList(fromIndex, toIndex);
  }

  @Override
  public ISortedMap<Integer, E> asMap() {
    return asList().asMap();
  }

  @Override
  public abstract S subSet(final E from, final boolean fromInclusive, final E to, final boolean toInclusive);

  @Override
  public Opt<E> tryGetLast() {
    final Iterator<E> it = descendingIterator();
    return it.hasNext() ? Opt.of(it.next()) : Opt.empty();
  }

  @Override
  public Opt<E> tryGetLower(final E e) {
    return descendingStream(e).tryFindFirst();
  }

  @Override
  public Opt<E> tryGetFloor(final E e) {
    return descendingStream().filter(e2->e2.compareTo(e)<=0).tryFindFirst();
  }

  @Override
  public Opt<E> tryGetCeiling(final E e) {
    return stream().filter(e2->e2.compareTo(e)>=0).tryFindFirst();
  }

  @Override
  public Opt<E> tryGetHigher(final E e) {
    return stream().filter(e2->e2.compareTo(e)>0).tryFindFirst();
  }

  @Override
  public ISortedSet<E> intersection(final Collection<?> other) {
    if(isEmpty() || other.isEmpty()) return emptySortedSet();
    else if(this==other) return this;
    else return parallelStream().filter(other::contains).collect(toISortedSet());
  }

  @Deprecated
  @Override
  public final boolean add(final E e) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public final boolean remove(final Object o) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public final boolean addAll(final Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public final boolean removeAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public final boolean retainAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public
  final void clear() {
    throw new UnsupportedOperationException();
  }
  @Deprecated
  @Override
  public final E pollFirst() {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public final E pollLast() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public final Comparator<E> comparator() {
    return null;
  }

  @Override
  @Deprecated
  public final NavigableSet<E> descendingSet(){
    throw new UnsupportedOperationException();
  }

}
