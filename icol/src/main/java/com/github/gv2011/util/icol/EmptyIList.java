package com.github.gv2011.util.icol;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

final class EmptyIList<E> extends AbstractList<E> implements IList<E>{

  @SuppressWarnings("rawtypes")
  private static final IList INSTANCE = new EmptyIList();

  @SuppressWarnings("unchecked")
  static <T> IList<T> empty() {
    return INSTANCE;
  }


  private EmptyIList(){}


  @Override
  public IList<E> subList(final int fromIndex, final int toIndex) {
    return this;
  }

  @Override
  public E get(final int index) {
    throw new IndexOutOfBoundsException();
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public boolean contains(final Object o) {
    return false;
  }

  @Override
  public int hashCode() {
    return 1;
  }

  @Override
  public Stream<E> stream() {
    return Stream.empty();
  }

  @Override
  public Stream<E> parallelStream() {
    return Stream.empty();
  }

  @Override
  public Iterator<E> iterator() {
    return EmptyIterator.instance();
  }

  @Override
  public ListIterator<E> listIterator() {
    return EmptyIterator.instance();
  }

  @Override
  public ListIterator<E> listIterator(final int index) {
    return EmptyIterator.instance();
  }

  @Override
  public boolean containsAll(final Collection<?> c) {
    return c.isEmpty();
  }


  @Override
  public Optional<E> asOptional() {
    return Optional.empty();
  }


  @Override
  public E single() {
    throw new NoSuchElementException();
  }


  @Override
  public E first() {
    throw new NoSuchElementException();
  }


  @Override
  public Optional<E> tryGetFirst() {
    return Optional.empty();
  }


  @Override
  public int indexOf(final Object o) {
    return -1;
  }


  @Override
  public int lastIndexOf(final Object o) {
    return -1;
  }


  @Override
  public Map<Integer, E> asMap() {
    return EmptyIMap.instance();
  }

}
