package com.github.gv2011.util.icol;

import java.util.Iterator;
import java.util.function.Function;

public final class MappingIterator<I,O> implements Iterator<O> {

  private final Iterator<I> delegate;
  private final Function<I,O> mapping;

  public MappingIterator(final Iterator<I> delegate, final Function<I, O> mapping) {
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

}
