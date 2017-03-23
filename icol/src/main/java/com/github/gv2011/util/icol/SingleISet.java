package com.github.gv2011.util.icol;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;

class SingleISet<E> extends AbstractSet<E> implements ISortedSet<E> {

  static <T> ISortedSet<T> create(final T element, final Comparator<? super T> comparator) {
    return new SingleISet<T>(element){
      @Override
      public Optional<Comparator<? super T>> tryGetComparator() {
        return Optional.of(comparator);
      }
      @Override
      public Comparator<? super T> comparator(){
        return comparator;
      }
    };
  }

  private final E element;

  SingleISet(final E element) {
    this.element = element;
  }

  @Override
  public Optional<E> tryGetFirst() {
    return Optional.of(element);
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
  public Stream<E> stream() {
    return Stream.of(element);
  }

  @Override
  public Stream<E> parallelStream() {
    return Stream.of(element);
  }

  @Override
  public void forEach(final Consumer<? super E> action) {
    action.accept(element);
  }

  @Override
  public E first() {
    return element;
  }

  @Override
  public E last() {
    return element;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public ISortedSet<E> subSet(final E fromElement, final E toElement) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public ISortedSet<E> headSet(final E toElement) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public ISortedSet<E> tailSet(final E fromElement) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Optional<E> tryGetLast() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Optional<Comparator<? super E>> tryGetComparator() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Iterator<E> iterator() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public int size() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }


}
