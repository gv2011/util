package com.github.gv2011.util.icol;

import static com.github.gv2011.util.icol.ICollections.toISortedSet;

import java.util.Collection;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
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




import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;

import com.github.gv2011.util.XStream;
public interface ISortedSet<E extends Comparable<? super E>> extends ISet<E>, NavigableSet<E>, ListAccess<E>{

  public static interface Builder<E extends Comparable<? super E>> extends CollectionBuilder<ISortedSet<E>,E,Builder<E>>{}

  @SuppressWarnings("unchecked")
  static <E extends Comparable<? super E>> ISortedSet<E>
  cast(final ISortedSet<? extends E> set){return (ISortedSet<E>) set;}

  @Override
  @Deprecated
  @SuppressWarnings({ "unchecked", "rawtypes" })
  default Comparator<E> comparator() {
    return (Comparator)Comparator.naturalOrder();
  }

  @Override
  default ISortedSet<E> subSet(final E fromElement, final E toElement){
    return this.subSet(fromElement, true, toElement, false);
  }

  @Override
  ISortedSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive);

  @Override
  default ISortedSet<E> headSet(final E toElement){
    return headSet(toElement, true);
  }

  @Override
  default ISortedSet<E> headSet(final E toElement, final boolean inclusive){
    if(isEmpty()) return this;
    else return subSet(first(), true, toElement, inclusive);
  }

  @Override
  default ISortedSet<E> tailSet(final E fromElement){
    return tailSet(fromElement, true);
  }

  @Override
  default ISortedSet<E> tailSet(final E fromElement, final boolean inclusive){
    if(isEmpty()) return this;
    else return subSet(fromElement, inclusive, last(), true);
  }


  @Override
  ISortedSet<E> descendingSet();

  @Override
  default Iterator<E> descendingIterator(){
    return descendingSet().iterator();
  }

  @Override
  default ISortedSet<E> subtract(final Collection<?> other) {
    if(other.isEmpty()) return this;
    else{
      return parallelStream().filter(e->!other.contains(e)).collect(toISortedSet());
    }
  }

  @Override
  XStream<E> stream();

  Opt<E> tryGetLast();

  @Override
  default E first(){
    if(isEmpty()) throw new NoSuchElementException();
    else return iterator().next();
  }

  @Override
  default E last(){
    if(isEmpty()) throw new NoSuchElementException();
    else return descendingIterator().next();
  }

  @Override
  default E lower(final E e) {
    return tryGetLower(e).get();
  }

  /**
   * Returns the greatest element in this set strictly less than the given element.
   */
  Opt<E> tryGetLower(E e);

  @Override
  default E floor(final E e) {
    return tryGetFloor(e).get();
  }

  /**
   * Returns the greatest element in this set less than or equal to the given element.
   */
  Opt<E> tryGetFloor(E e);

  @Override
  default E ceiling(final E e) {
    return tryGetCeiling(e).get();
  }

  /**
   * Returns the least element in this set greater than or equal to the given element.
   */
  Opt<E> tryGetCeiling(E e);

  @Override
  default E higher(final E e) {
    return tryGetHigher(e).get();
  }

  /**
   * Returns the least element in this set strictly greater than the given element.
   */
  Opt<E> tryGetHigher(E e);

  @Deprecated
  @Override
  default E pollFirst() {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default E pollLast() {
    throw new UnsupportedOperationException();
  }

  @Override
  default E get(final int index){
    //TODO more efficient implementation
    final Iterator<E> it = iterator();
    for(int i=0; i<index; i++) it.next();
    return it.next();
  }

  @Override
  default int indexOf(final Object child){
    //TODO more efficient implementation
    final Iterator<E> it = this.iterator();
    int result = -1;
    int i=0;
    while(result==-1 && it.hasNext()){
      if(it.next().equals(child)) result=i;
      i++;
    }
    return result;
  }
  
  
  @Override
  ISortedSet<E> intersection(Collection<?> other);


}
