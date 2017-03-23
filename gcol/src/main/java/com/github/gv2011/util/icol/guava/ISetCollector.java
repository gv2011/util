package com.github.gv2011.util.icol.guava;

import java.util.Set;
import java.util.function.Supplier;

import com.github.gv2011.util.icol.AbstractCollectionCollector;
import com.github.gv2011.util.icol.ISet;

final class ISetCollector<T> extends AbstractCollectionCollector<ISet<T>, T, ISet.Builder<T>>{

  private static final ISet<Characteristics> CHARACTERISTICS =
    new ISetBuilder<Characteristics>().add(Characteristics.CONCURRENT).add(Characteristics.UNORDERED).build()
  ;

  ISetCollector() {super(TRY_ADD);}

  @Override
  public Supplier<ISet.Builder<T>> supplier() {
    return ISetBuilder::new;
  }

  @Override
  public Set<Characteristics> characteristics() {
    return CHARACTERISTICS;
  }

}
