package com.github.gv2011.util.gcol;


import com.github.gv2011.util.icol.AbstractISortedMap;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.icol.Opt;

final class ListMap<E> extends AbstractISortedMap<Integer,E> implements ISortedMap<Integer, E>{

  private final IList<E> list;

  ListMap(final IList<E> list) {
    assert !list.isEmpty();
    this.list = list;
  }

  @Override
  public ISortedSet<Integer> keySet() {
    return new IntegerSet(0, list.size());
  }

  @Override
  public IList<E> values() {
    return list;
  }

  @Override
  public Opt<E> tryGet(final Object key) {
    return Opt.tryCast(key, Integer.class).flatMap(i->i>=0 && i<list.size() ? Opt.of(list.get(i)) : Opt.empty());
  }

}
