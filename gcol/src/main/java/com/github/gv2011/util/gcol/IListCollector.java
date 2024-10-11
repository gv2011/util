package com.github.gv2011.util.gcol;

import java.util.Set;
import java.util.function.Supplier;

import com.github.gv2011.util.icol.AbstractCollectionCollector;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IList.Builder;
import com.github.gv2011.util.icol.ISet;

final class IListCollector<T> extends AbstractCollectionCollector<IList<T>, T, IList.Builder<T>> {

  private static final ISet<Characteristics> CHARACTERISTICS =
    new ISetBuilder<Characteristics>().add(Characteristics.CONCURRENT).build()
  ;

  IListCollector() {super(ADD);}

  @Override
  public Set<Characteristics> characteristics() {
    return CHARACTERISTICS;
  }

  @Override
  public Supplier<Builder<T>> supplier() {
    return IListBuilder::new;
  }


}
