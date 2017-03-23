package com.github.gv2011.util.icol;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntIterator implements Iterator<Integer>{

  private final int upperExcl;
  private int current = 0;

  public IntIterator(final int upperExcl) {
    this.upperExcl = upperExcl;
  }

  @Override
  public boolean hasNext() {
    return current<upperExcl;
  }

  @Override
  public Integer next() {
    if(!hasNext()) throw new NoSuchElementException();
    return current++;
  }

}
