package com.github.gv2011.util.icol;

import static com.github.gv2011.util.icol.ICollections.iCollections;
import static com.github.gv2011.util.icol.ICollections.xStream;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.gv2011.util.XStream;

public final class ArrayISortedSet<E extends Comparable<? super E>> extends AbstractArrayISortedSet<E,ISortedSet<E>>{

  private final Object[] elements;

  public ArrayISortedSet(final Collection<? extends E> elements) {
    this(elements.parallelStream());
  }

  public ArrayISortedSet(final Stream<? extends E> elements) {
    this(elements.sorted().toArray());
  }

  private ArrayISortedSet(final Object[] elements) {
    this.elements = elements;
  }

  @Override
  @SuppressWarnings("unchecked")
  public E get(final int index){
    return (E) elements[index];
  }

  @Override
  public boolean isEmpty() {
    return elements.length==0;
  }

  @Override
  public int size() {
    return elements.length;
  }



  @Override
  public E first() {
    if(isEmpty()) throw new NoSuchElementException();
    return get(0);
  }

  @Override
  public E last() {
    if(isEmpty()) throw new NoSuchElementException();
    return get(size()-1);
  }

  @Override
  public Opt<E> tryGetFirst() {
    return isEmpty() ? Opt.empty() : Opt.of(get(0));
  }

  @Override
  public Opt<E> tryGetLast() {
    return isEmpty() ? Opt.empty() : Opt.of(get(size()-1));
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public XStream<E> stream() {
    return (XStream) XStream.ofArray(elements);
  }

  @Override
  public XStream<E> descendingStream() {
    final int l = elements.length;
    IntStream.range(1, l-1).map(i->l-i).mapToObj(i->elements[i]);
    return xStream(IntStream.range(1, l-1).map(i->l-i).mapToObj(this::get));
  }

  @Override
  public XStream<E> descendingStream(final E startExclusive) {
    //TODO more efficient
    return descendingStream().filter(e->e.compareTo(startExclusive)<0);
  }

  @Override
  public boolean contains(final Object o) {
    return parallelStream().filter(e->e.equals(o)).tryFindAny().isPresent();
  }

  @Override
  public boolean containsElement(final E element) {
    return tryGetFloor(element).map(f->f.equals(element)).orElse(false);
  }

  @Override
  public ISortedSet<E> addElement(final E other) {
    return containsElement(other) ? this : new ArrayISortedSet<>(parallelStream());
  }

  @Override
  public ISortedSet<E> subSet(final E from, final boolean fromInclusive, final E to, final boolean toInclusive) {
    if(isEmpty()) return iCollections().emptySortedSet();
    else{
      final int lastIndex = size()-1;

      //left limit:
      int start = Arrays.binarySearch(elements, from);
      if(start<0){ //set does not contain element
        start = -start-1;
      }
      else{
        if(!fromInclusive) start++;//set contains from, but must not be included
      }

      //right limit:
      int end;
      if(start>lastIndex) end = start;
      else{
        end = Arrays.binarySearch(elements, to);
        if(end<0) {
          end = -end - 1;
        }
        else{
          if(toInclusive) end++;//set contains to, and it must be included
        }
      }
      final int size = end-start;
      if(size<=0) return ICollections.emptySortedSet();
      else if(size==elements.length){
        return this;
      }
      else{
        final Object[] array = new Object[size];
        System.arraycopy(elements, start, array, 0, size);
        return new ArrayISortedSet<E>(array);
      }
    }
  }

  @Override
  public String toString() {
    return Arrays.toString(elements);
  }

  @Override
  public boolean equals(final Object obj) {
    if(this==obj) return true;
    else if(!(obj instanceof Set)) return false;
    else if(obj instanceof ArrayISortedSet){
      return Arrays.deepEquals(elements, ((ArrayISortedSet<?>)obj).elements);
    }
    else{
      final Set<?> other = (Set<?>) obj;
      if(other.size()!=size()) return false;
      else return containsAll(other);
    }
  }

}
