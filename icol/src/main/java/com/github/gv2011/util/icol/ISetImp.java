package com.github.gv2011.util.icol;

import java.util.Set;
import java.util.function.Function;

final class ISetImp<E> extends AbstractISet<Set<E>,E,E>{

  private final Set<E> delegate;

  ISetImp(final Set<E> delegate) {
    assert delegate.size()>1;
    this.delegate = delegate;
  }

  @Override
  protected Set<E> delegate() {
    return delegate;
  }

  @Override
  protected final Function<E, E> mapping() {
    return Function.identity();
  }

  @Override
  public boolean contains(final Object o) {
    return delegate.contains(o);
  }

}
