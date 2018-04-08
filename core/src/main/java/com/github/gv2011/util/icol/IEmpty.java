package com.github.gv2011.util.icol;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
import static com.github.gv2011.util.CollectionUtils.iCollections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.icol.ISortedMap;

final class IEmpty<E> implements Opt<E>{

  @SuppressWarnings("rawtypes")
  static final Opt INSTANCE = new IEmpty();

  @SuppressWarnings("rawtypes")
  private static final ListIterator EMPTY_ITERATOR = new EmptyIterator();

  private IEmpty(){}

  @Override
  public IEmpty<E> subList(final int fromIndex, final int toIndex) {
    if(fromIndex!=0 || toIndex!=0) throw new IndexOutOfBoundsException();
    return this;
  }

  @Override
  public ISortedMap<Integer, E> asMap() {
    return iCollections().<Integer, E>emptySortedMap();
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
    return EMPTY_ITERATOR;
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
  public E get() {
    throw new NoSuchElementException();
  }

  @Override
  public boolean isPresent() {
    return false;
  }

  @Override
  public IEmpty<E> filter(final Predicate<? super E> predicate) {
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <U> Opt<U> map(final Function<? super E, ? extends U> mapper) {
    return (Opt<U>) this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <U> Opt<U> flatMap(final Function<? super E, ? extends Opt<? extends U>> mapper) {
    return (Opt<U>) this;
  }

  @Override
  public E orElse(final E other) {
    return other;
  }

  @Override
  public E orElseGet(final Supplier<? extends E> supplier) {
    return supplier.get();
  }

  @Override
  public <X extends Throwable> E orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X {
    throw exceptionSupplier.get();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Opt<E> or(final Supplier<? extends Opt<? extends E>> supplier) {
    return (Opt<E>) supplier.get();
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
