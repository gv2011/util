package com.github.gv2011.util.gcol;

import java.util.Set;
import java.util.function.Supplier;

import com.github.gv2011.util.icol.AbstractCollectionCollector;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISetList;

final class ISetListCollector<T> extends AbstractCollectionCollector<ISetList<T>, T, ISetList.Builder<T>> {

  private static final ISet<Characteristics> CHARACTERISTICS =
    new ISetBuilder<Characteristics>().add(Characteristics.CONCURRENT).build()
  ;

  ISetListCollector() {super(ADD);}

  @Override
  public Set<Characteristics> characteristics() {
    return CHARACTERISTICS;
  }

  @Override
  public Supplier<ISetList.Builder<T>> supplier() {
    return ISetListBuilder::new;
  }


}
