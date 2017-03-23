package com.github.gv2011.util.icol.guava;

import static com.github.gv2011.util.ex.Exceptions.notYetImplemented;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISortedMap;

final class IListWrapper<E> implements IList<E>{

  private final List<E> delegate;

  IListWrapper(final List<E> delegate) {
    this.delegate = delegate;
  }

  @Override
  public void forEach(final Consumer<? super E> action) {
    delegate.forEach(action);
  }

  @Override
  public int size() {
    return delegate.size();
  }

  @Override
  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  @Override
  public boolean contains(final Object o) {
    return delegate.contains(o);
  }

  @Override
  public Iterator<E> iterator() {
    return delegate.iterator();
  }

  @Override
  public Object[] toArray() {
    return delegate.toArray();
  }

  @Override
  public <T> T[] toArray(final T[] a) {
    return delegate.toArray(a);
  }

  @Override
  public boolean containsAll(final Collection<?> c) {
    return delegate.containsAll(c);
  }

  @Override
  public boolean equals(final Object o) {
    return delegate.equals(o);
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public E get(final int index) {
    return delegate.get(index);
  }

  @Override
  public Stream<E> stream() {
    return delegate.stream();
  }

  @Override
  public Stream<E> parallelStream() {
    return delegate.parallelStream();
  }

  @Override
  public int indexOf(final Object o) {
    return delegate.indexOf(o);
  }

  @Override
  public int lastIndexOf(final Object o) {
    return delegate.lastIndexOf(o);
  }

  @Override
  public ListIterator<E> listIterator() {
    return delegate.listIterator();
  }

  @Override
  public ListIterator<E> listIterator(final int index) {
    return delegate.listIterator(index);
  }

  @Override
  public IList<E> subList(final int fromIndex, final int toIndex) {
    return new IListWrapper<>(delegate.subList(fromIndex, toIndex));
  }

  @Override
  public Spliterator<E> spliterator() {
    return delegate.spliterator();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public ISortedMap<Integer, E> asMap() {
    return notYetImplemented();
  }


}
