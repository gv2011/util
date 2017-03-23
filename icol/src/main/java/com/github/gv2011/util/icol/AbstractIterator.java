package com.github.gv2011.util.icol;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

public final class AbstractIterator<I,O> implements Iterator<O>{

  private final Iterator<I> delegate;
  private final Function<I,O> mapping;

  AbstractIterator(final Iterator<I> delegate, final Function<I,O> mapping) {
    this.delegate = delegate;
    this.mapping = mapping;
  }

  @Override
  public boolean hasNext() {
    return delegate.hasNext();
  }

  @Override
  public O next() {
    return mapping.apply(delegate.next());
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

}
