package com.github.gv2011.util.icol.guava;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedMap.Builder;

final class ISortedMapCollector<K extends Comparable<?>,V,T>
extends AbstractMapCollector<ISortedMap<K,V>, K, V, ISortedMap.Builder<K,V>,T>{

  private static final ISet<Characteristics> CHARACTERISTICS =
    new ISetBuilder<Characteristics>().add(Characteristics.CONCURRENT).add(Characteristics.UNORDERED).build()
  ;

  ISortedMapCollector(
    final Function<? super T, ? extends K> keyMapper,
    final Function<? super T, ? extends V> valueMapper
  ) {
    super(keyMapper, valueMapper);
  }

  @Override
  public Supplier<Builder<K, V>> supplier() {
    return ISortedMapBuilder::new;
  }

  @Override
  public Set<Characteristics> characteristics() {
    return CHARACTERISTICS;
  }

}
