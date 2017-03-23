package com.github.gv2011.util.icol;

import java.util.ListIterator;
import java.util.NoSuchElementException;

final class EmptyIterator<E> implements ListIterator<E>{

  @SuppressWarnings("rawtypes")
  private static final EmptyIterator INSTANCE = new EmptyIterator();

  @SuppressWarnings("unchecked")
  static <E> ListIterator<E> instance() {
    return INSTANCE;
  }


  private EmptyIterator(){}

  @Override
  public boolean hasNext() {
    return false;
  }

  @Override
  public E next() {
    throw new NoSuchElementException();
  }

  @Override
  public boolean hasPrevious() {
    return false;
  }

  @Override
  public E previous() {
    throw new NoSuchElementException();
  }

  @Override
  public int nextIndex() {
    return 0;
  }

  @Override
  public int previousIndex() {
    return -1;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void set(final E e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(final E e) {
    throw new UnsupportedOperationException();
  }


}
