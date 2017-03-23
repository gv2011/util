package com.github.gv2011.util.icol.guava;

import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.IMap.Builder;
import com.google.common.collect.ImmutableMap;

final class IMapBuilder<K,V> extends AbstractIMapBuilder<IMap<K,V>, K, V, IMap.Builder<K,V>>
implements IMap.Builder<K,V>{

  @Override
  protected Builder<K, V> self() {
    return this;
  }

  @Override
  public IMap<K, V> build() {
    synchronized(map){
      return new IMapWrapper<>(ImmutableMap.copyOf(map));
    }
  }

}
