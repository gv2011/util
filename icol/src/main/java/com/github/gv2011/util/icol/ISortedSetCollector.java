package com.github.gv2011.util.icol;

import java.util.Collections;
import java.util.NavigableSet;
import java.util.function.Function;

import com.github.gv2011.util.CollectionUtils.SortedSetCollector;

public final class ISortedSetCollector<T> extends SortedSetCollector<T,ISortedSet<T>>{

  @Override
  public Function<NavigableSet<T>, ISortedSet<T>> finisher() {
    return s->new ISortedSetImp<>(Collections.unmodifiableSortedSet(s));
  }
}
