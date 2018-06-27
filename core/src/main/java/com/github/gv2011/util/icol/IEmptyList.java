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

import static com.github.gv2011.util.icol.ICollections.iCollections;
import static com.github.gv2011.util.icol.IEmpty.EMPTY_ITERATOR;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

final class IEmptyList<E> extends AbstractIList<E> implements IList<E>{

  @SuppressWarnings("rawtypes")
  static final IEmptyList INSTANCE = new IEmptyList();

  private IEmptyList(){}

  @Override
  public IEmptyList<E> subList(final int fromIndex, final int toIndex) {
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
  public IList<E> addElement(final E element) {
    return ICollections.listOf(element);
  }

}
