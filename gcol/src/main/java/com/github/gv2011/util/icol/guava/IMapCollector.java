package com.github.gv2011.util.icol.guava;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.ISet;

final class IMapCollector<K,V,T>
extends AbstractMapCollector<IMap<K,V>, K, V, IMap.Builder<K,V>,T>{

  private static final ISet<Characteristics> CHARACTERISTICS =
    new ISetBuilder<Characteristics>().add(Characteristics.CONCURRENT).add(Characteristics.UNORDERED).build()
  ;

  IMapCollector(
    final Function<? super T, ? extends K> keyMapper,
    final Function<? super T, ? extends V> valueMapper
  ) {
    super(keyMapper, valueMapper);
  }

  @Override
  public Supplier<IMap.Builder<K, V>> supplier() {
    return IMapBuilder::new;
  }

  @Override
  public Set<Characteristics> characteristics() {
    return CHARACTERISTICS;
  }

}
