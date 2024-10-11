package com.github.gv2011.util.gcol;

import java.util.Set;
import java.util.function.Supplier;

import com.github.gv2011.util.icol.AbstractCollectionCollector;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedSet;

final class ISortedSetCollector<T extends Comparable<? super T>>
extends AbstractCollectionCollector<ISortedSet<T>, T, ISortedSet.Builder<T>>{

  private static final ISet<Characteristics> CHARACTERISTICS =
    new ISetBuilder<Characteristics>().add(Characteristics.CONCURRENT).add(Characteristics.UNORDERED).build()
  ;

  ISortedSetCollector() {super(TRY_ADD);}

  @Override
  public Set<Characteristics> characteristics() {
    return CHARACTERISTICS;
  }

  @Override
  public Supplier<ISortedSet.Builder<T>> supplier() {
    return ISortedSetBuilder::new;
  }


}
