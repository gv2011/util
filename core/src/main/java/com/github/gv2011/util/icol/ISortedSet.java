package com.github.gv2011.util.icol;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface ISortedSet<E> extends ISet<E>, NavigableSet<E>{

  public static interface Builder<E> extends CollectionBuilder<ISortedSet<E>,E,Builder<E>>{}

  @Override
  @Deprecated
  default Comparator<? super E> comparator() {
    throw new UnsupportedOperationException();
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


  Optional<E> tryGetLast();

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
  Optional<E> tryGetLower(E e);

  @Override
  default E floor(final E e) {
    return tryGetFloor(e).get();
  }

  /**
   * Returns the greatest element in this set less than or equal to the given element.
   */
  Optional<E> tryGetFloor(E e);

  @Override
  default E ceiling(final E e) {
    return tryGetCeiling(e).get();
  }

  /**
   * Returns the least element in this set greater than or equal to the given element.
   */
  Optional<E> tryGetCeiling(E e);

  @Override
  default E higher(final E e) {
    return tryGetHigher(e).get();
  }

  /**
   * Returns the least element in this set strictly greater than the given element.
   */
  Optional<E> tryGetHigher(E e);

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

  default E get(final int index){
    //TODO more efficient implementation
    final Iterator<E> it = iterator();
    for(int i=0; i<index; i++) it.next();
    return it.next();
  }

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

}
