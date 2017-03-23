package com.github.gv2011.util.icol;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;

public interface ICollection<E> extends Collection<E>{

  default Optional<E> asOptional(){
    final int size = size();
    if(size==0) return Optional.empty();
    else if(size==1) return Optional.of(iterator().next());
    else throw new IllegalStateException();
  }

  default E single(){
    return asOptional().get();
  }

  default E first(){
    if(isEmpty()) throw new NoSuchElementException();
    else return iterator().next();
  }

  default Optional<E> tryGetFirst(){
    if(isEmpty()) return Optional.empty();
    else return Optional.of(iterator().next());
  }



  @Override
  @Deprecated
  default boolean add(final E e) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default boolean addAll(final Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default boolean remove(final Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default boolean removeAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default boolean removeIf(final Predicate<? super E> filter) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default boolean retainAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default <T> T[] toArray(final T[] a) {
    throw new UnsupportedOperationException();
  }


}
