package com.github.gv2011.util.icol;

import static com.github.gv2011.util.ex.Exceptions.bug;
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
  @SuppressWarnings("unchecked")
  public XStream<E> stream() {
    return (XStream<E>) XStream.ofArray(elements);
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
      final E last = last();

      //left limit:
      int start = floorIndex(from);
      if(start==-1){ //from is not in set range
        final int fromIsRightOfLast = from.compareTo(last);
        if(fromIsRightOfLast>0) start = size(); //size: index of last; result is empty
        else if(fromIsRightOfLast<0){
          // 1. The set is not emty
          // 2. from is not between first and last of set
          // 3. from is not at the right
          // -> from is at the left. Set start to first element of set
          start = 0;
        }
        else bug();//from is not in set
      }
      else{
        if(!fromInclusive){
          final E left = get(start);
          if(left.equals(from)) start++;
        }
      }

      //right limit:
      int end;
      if(start>lastIndex) end = start;
      else{
        end = floorIndex(to);
        if(end==-1) {
          if(to.compareTo(get(0))<0) end = start;
          else{
            assert to.compareTo(last) > 0;
            end = size();
          }
        }
        else{
          if(to.equals(get(end))){
            if(toInclusive) end++;
          }
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

  /**
   * Index of element or index left from element.
   *
   * Returns the index of the greatest element in this set less than or equal to
   * the given element, or -1 if there is no such element.
   */
  private int floorIndex(final E element) {
    final int result;
    final int length = elements.length;
    if(length==0) result = -1; //element not in empty set
    else{
      final int lastIndex = length-1;
      int cr = element.compareTo(get(lastIndex));
      if(cr>=0) result = lastIndex;//element is greater than or equal to last of set
      else{
        cr = element.compareTo(get(0));
        if(cr==0) result = 0; //element is first of set
        else if(cr<0) result = -1; //element is less than first of set
        else{ //element could be in the set
          int from = 0;
          int to = elements.length;
          int size = to-from;
          while(size>1){
            final int i = from + size / 2;
            cr = get(i).compareTo(element);
            if(cr==0){
              from = i; to = i+1; //found
            }
            else if(cr<0){//i is less than element, but there may be better
              // ones to the right
              from = i;
            }
            else{//element must be on the left of i
              to = i;
            }
            size = to-from;
          }
          if(size<=0) result = -1;
          else result = lessOrEqual(get(from),element) ? from : -1;
        }
      }
    }
    return result;
  }

  private boolean lessOrEqual(final E e1, final E e2){
    return e1.compareTo(e2)<=0;
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
