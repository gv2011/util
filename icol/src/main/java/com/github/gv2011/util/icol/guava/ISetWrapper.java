package com.github.gv2011.util.icol.guava;

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
    return delegate.containsAll(c);
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
