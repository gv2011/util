package com.github.gv2011.util.gcol;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.github.gv2011.util.icol.Nothing;


@SuppressWarnings("rawtypes")
final class IEmpty implements Nothing{

  static final IEmpty INSTANCE = new IEmpty();
  static final ListIterator EMPTY_ITERATOR = new EmptyIterator();

  @Override
  public Iterator iterator() {
    return EMPTY_ITERATOR;
  }

  @Override
  public int hashCode() {
    return Nothing.HASH_CODE;
  }

  @Override
  public boolean equals(final Object o) {
    if(this==o) return true;
    else if(!Set.class.isInstance(o)) return false;
    else return Set.class.cast(o).isEmpty();
  }

  @Override
  public String toString() {
     return Nothing.STRING_VALUE;
  }


  private static final class EmptyIterator<T> implements ListIterator<T>{
    @Override
    public boolean hasNext() {
      return false;
    }
    @Override
    public T next() {
      throw new NoSuchElementException();
    }
    @Override
    public boolean hasPrevious() {
      return false;
    }
    @Override
    public T previous() {
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
    public void set(final T e) {
      throw new UnsupportedOperationException();
    }
    @Override
    public void add(final T e) {
      throw new UnsupportedOperationException();
    }
  }
}
