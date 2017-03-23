package com.github.gv2011.util.icol.guava;

import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedMap.Builder;
import com.google.common.collect.ImmutableSortedMap;

final class ISortedMapBuilder<K,V> extends AbstractIMapBuilder<ISortedMap<K,V>, K, V, ISortedMap.Builder<K,V>>
implements ISortedMap.Builder<K,V>{

  @Override
  protected Builder<K, V> self() {
    return this;
  }

  @Override
  public ISortedMap<K, V> build() {
    synchronized(map){
      return new ISortedMapWrapper<>(ImmutableSortedMap.copyOf(map));
    }
  }

}
