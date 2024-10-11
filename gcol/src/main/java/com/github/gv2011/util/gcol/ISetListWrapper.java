package com.github.gv2011.util.gcol;

import static com.github.gv2011.util.icol.ICollections.emptySortedMap;
import static com.github.gv2011.util.icol.ICollections.toISet;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;

import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.ex.ThrowingFunction;
import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISetList;
import com.github.gv2011.util.icol.ISortedMap;
import com.google.common.collect.ImmutableSet;

class ISetListWrapper<E> implements ISetList<E>{

  static final <E> ISetList<E> wrap(final List<E> delegate){
    return delegate.isEmpty() ? ICollections.emptyList() : new ISetListWrapper<>(delegate);
  }

  final List<E> delegate;

  @Nullable Integer hashCode = null;

  protected ISetListWrapper(final List<E> delegate) {
    assert !delegate.isEmpty();
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
  public final <F> ISet<F> map(final ThrowingFunction<? super E, ? extends F> mapping) {
    return stream().map(mapping.asFunction()).collect(toISet());
  }

  @Override
  public ISet<E> intersection(final Collection<?> other) {
    return ICollections.intersection(this, other);
  }

  @Override
  public boolean equals(final Object o) {
    return delegate.equals(o);
  }

  @Override
  public int hashCode() {
    final @Nullable Integer cached = hashCode;
    final int result;
    if(cached==null){
      result = delegate.hashCode();
      hashCode = result;
    }
    else{
      result = cached;
      assert result == delegate.hashCode();
    }
    return result;
  }

  @Override
  public E get(final int index) {
    return delegate.get(index);
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
  public ISetList<E> subList(final int fromIndex, final int toIndex) {
    return wrap(delegate.subList(fromIndex, toIndex));
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
    return isEmpty() ? emptySortedMap() : new ListMap<E>(this);
  }

  @Override
  public ISet<E> asSet() {
    return new ISetWrapper<>(ImmutableSet.copyOf(delegate));
  }

  @Override
  public ISetList<E> reversed() {
    return new ISetListWrapper<>(
      delegate.getClass().equals(RList.class)
      ? ((RList<E>)delegate).delegate
      : new RList<>(delegate)
    );
  }

}
