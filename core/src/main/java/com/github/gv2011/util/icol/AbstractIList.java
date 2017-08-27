package com.github.gv2011.util.icol;

import static com.github.gv2011.util.CollectionUtils.toIList;
import static com.github.gv2011.util.CollectionUtils.toISortedMap;
import static com.github.gv2011.util.Verify.verify;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.stream.IntStream;

public abstract class AbstractIList<E> implements IList<E>{

  @Override
  public abstract int size();

  @Override
  public abstract E get(final int index);

  @Override
  public IList<E> subList(final int fromIndex, final int toIndex) {
    return IntStream.range(fromIndex, toIndex).mapToObj(this::get).collect(toIList());
  }


  @Override
  public boolean isEmpty() {
    return size()==0;
  }

  @Override
  public boolean contains(final Object o) {
    if(isEmpty()) return false;
    else return stream().anyMatch(o::equals);
  }

  @Override
  public Iterator<E> iterator() {
    return listIterator();
  }

  @Override
  public Object[] toArray() {
    final Object[] result = new Object[size()];
    for(int i=0; i<size(); i++) result[i] = get(i);
    return result;
  }

  @Override
  public boolean containsAll(final Collection<?> c) {
    if(this==c) return true;
    else if(isEmpty()) return c.isEmpty();
    else{
      return c.stream().allMatch(this::contains);
    }
  }


  @Override
  public int indexOf(final Object o) {
    return IntStream.range(0, size()).filter(i->o.equals(get(i))).findFirst().orElse(-1);
  }

  @Override
  public int lastIndexOf(final Object o) {
    final int s = size();
    return IntStream.range(1, s-1).map(i->s-i).filter(i->o.equals(get(i))).findFirst().orElse(-1);
  }

  @Override
  public ListIterator<E> listIterator() {
    return listIterator(0);
  }

  @Override
  public ListIterator<E> listIterator(final int index) {
    return new ListIterator<E>(){
      private int i = index;
      @Override
      public boolean hasNext() {
        return i<size();
      }
      @Override
      public E next() {
        verify(hasNext());
        return get(i++);
      }
      @Override
      public boolean hasPrevious() {
        return i>0;
      }
      @Override
      public E previous() {
        verify(hasPrevious());
        return get(--i);
      }
      @Override
      public int nextIndex() {
        return i;
      }

      @Override
      public int previousIndex() {
        return i-1;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }

      @Override
      public void set(final E e) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void add(final E e) {
        throw new UnsupportedOperationException();
      }

    };
  }

  @Override
  public ISortedMap<Integer, E> asMap() {
    return IntStream.range(0, size()).parallel().boxed().collect(toISortedMap(
      i->i,
      this::get
    ));
  }


}