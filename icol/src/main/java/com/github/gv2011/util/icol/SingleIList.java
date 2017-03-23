package com.github.gv2011.util.icol;

import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

final class SingleIList<E> extends AbstractList<E> implements IList<E>{

  private final E element;

  SingleIList(final E element){
    this.element = element;
  }

  @Override
  public IList<E> subList(final int fromIndex, final int toIndex) {
    if(fromIndex==0 && toIndex==1) return this;
    else return EmptyIList.empty();
  }

  @Override
  public E get(final int index) {
    if(index!=0) throw new IndexOutOfBoundsException();
    return element;
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public boolean contains(final Object o) {
    return element.equals(o);
  }

  @Override
  public int hashCode() {
    return 31+element.hashCode();
  }

  @Override
  public Stream<E> stream() {
    return Stream.of(element);
  }

  @Override
  public Stream<E> parallelStream() {
    return Stream.of(element);
  }

  @Override
  public Iterator<E> iterator() {
    return EmptyIterator.instance();
  }

  @SuppressWarnings("unchecked")
  @Override
  public ListIterator<E> listIterator() {
    return EmptyIterator.instance();
  }

  @SuppressWarnings("unchecked")
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
    return Optional.of(element);
  }


  @Override
  public E single() {
    return element;
  }


  @Override
  public E first() {
    return element;
  }


  @Override
  public Optional<E> tryGetFirst() {
    return Optional.of(element);
  }


  @Override
  public int indexOf(final Object o) {
    return element.equals(o)?0:-1;
  }


  @Override
  public int lastIndexOf(final Object o) {
    return element.equals(o)?0:-1;
  }


  @Override
  public IMap<Integer, E> asMap() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

}
