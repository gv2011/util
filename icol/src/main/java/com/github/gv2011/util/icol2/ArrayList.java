package com.github.gv2011.util.icol2;

import com.github.gv2011.util.icol.IList;

final class ArrayList<E> extends AbstractList<E>{

  private final E[] array;
  private boolean reverse;

  ArrayList(final E[] array) {
    this(array, false);
  }

  ArrayList(final E[] array, final boolean reverse) {
    this.array = array;
    this.reverse = reverse;
  }

  @Override
  public E get(final int index) {
    return reverse ? array[array.length-index-1] : array[index];
  }

  @Override
  public int size() {
    return array.length;
  }

  @Override
  public IList<E> subList(final int fromIndex, final int toIndex) {
    if(fromIndex==0 && toIndex==array.length) return this;
    else{
      if (fromIndex < 0) throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
      if (toIndex > array.length) throw new IndexOutOfBoundsException("toIndex = " + toIndex);
      if (fromIndex > toIndex){
        throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
      }
      final int length = toIndex-fromIndex;
      @SuppressWarnings("unchecked")
      final E[] dest = (E[]) new Object[length];
      if(reverse){
        for(int i=0; i<length; i++){
          dest[i] = array[array.length-(i+fromIndex+1)];
        }
      }else System.arraycopy(array, fromIndex, dest, 0, length);
      return new ArrayList<>(dest);
    }
  }

}
