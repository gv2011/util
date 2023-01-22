package com.github.gv2011.util.gcol;

import static com.github.gv2011.util.gcol.IEmpty.EMPTY_ITERATOR;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.github.gv2011.util.icol.AbstractIList;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedMap;

final class IEmptyList<E> extends AbstractIList<E>{

  @SuppressWarnings("rawtypes")
  static final IEmptyList INSTANCE = new IEmptyList();

  private IEmptyList(){}

  @Override
  public IEmptyList<E> subList(final int fromIndex, final int toIndex) {
    if(fromIndex!=0 || toIndex!=0) throw new IndexOutOfBoundsException();
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public ISortedMap<Integer, E> asMap() {
    return ISortedMapWrapper.EMPTY;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public boolean contains(final Object o) {
    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator<E> iterator() {
    return IEmpty.EMPTY_ITERATOR;
  }

  @Override
  public Object[] toArray() {
    return new Object[0];
  }

  @Override
  public String toString() {
    return "[]";
  }

  @Override
  public int hashCode() {
    return 31;
  }

  @Override
  public boolean equals(final Object obj) {
    if(this==obj) return true;
    else if(!(obj instanceof List)) return false;
    else return ((List<?>) obj).isEmpty();
  }

  @Override
  public boolean containsAll(final Collection<?> c) {
    return c.isEmpty();
  }

  @Override
  public E get(final int index) {
    throw new IndexOutOfBoundsException();
  }

  @Override
  public int indexOf(final Object o) {
    return -1;
  }

  @Override
  public int lastIndexOf(final Object o) {
    return -1;
  }

  @SuppressWarnings("unchecked")
  @Override
  public ListIterator<E> listIterator() {
    return EMPTY_ITERATOR;
  }

  @SuppressWarnings("unchecked")
  @Override
  public ListIterator<E> listIterator(final int index) {
    return EMPTY_ITERATOR;
  }

  @Override
  public IList<E> addElement(final E element) {
    return GuavaIcolFactory.INSTANCE.listOf(element);
  }

  @SuppressWarnings("unchecked")
  @Override
  public ISet<E> intersection(final Collection<?> other) {
    return IEmpty.INSTANCE;
  }

  @Override
  public IEmptyList<E> reversed() {
    return this;
  }

}
