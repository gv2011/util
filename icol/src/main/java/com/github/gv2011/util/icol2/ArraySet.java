package com.github.gv2011.util.icol2;

import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.util.Comparator;
import java.util.Optional;

import com.github.gv2011.util.icol.IIterator;
import com.github.gv2011.util.icol.ISortedSet;

public class ArraySet<E> extends AbstractSortedSet<E>{

  private final E[] elements;
  private final Comparator<? super E> cmp;

  ArraySet(final E[] elements, final Comparator<? super E> comparator) {
    this.elements = elements;
    this.cmp = comparator;
  }

  @Override
  public ISortedSet<E> subSet(final E fromElement, final boolean fromInclusive, final E toElement, final boolean toInclusive) {
    final int f = fromInclusive ? ceilingIx(fromElement) : higherIx(fromElement);
    throw notYetImplementedException();
  }

  private int floorIx(final E e) {
    if(isEmpty()) return -1;
    else{
      final int c = cmp.compare(first(), e);
      if(c==0) return 0;
      else if(c) return -1;
      else if(cmp.compare(first(), e)>0) return 0;
      else{
        final int r = -1;
        final int lower = 0;
        final int higher = size();
        final int middle = higher/2;
        while(r==-1){
          if(cmp.co
        }
        return r;
      }
    }
  }

  private int ceilingIx(final E e) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  private int index(final E fromElement, final boolean fromInclusive) {
    if()
    throw notYetImplementedException();
  }


  @Override
  public ISortedSet<E> descendingSet() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Optional<E> tryGetLast() {
    return isEmpty()?Optional.empty():Optional.of(elements[elements.length-1]);
  }

  @Override
  public Optional<E> tryGetlower(final E e) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Optional<E> tryGetFloor(final E e) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Optional<E> tryGetCeiling(final E e) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Optional<E> tryGetHigher(final E e) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public IIterator<E> iterator() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public int size() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

}
