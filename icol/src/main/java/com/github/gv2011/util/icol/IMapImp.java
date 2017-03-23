package com.github.gv2011.util.icol;

import java.util.Map;

final class IMapImp<K,V> extends AbstractIMap<Map<K,V>, K,V> implements IMap<K,V>{

  private Map<K, V> delegate;

  @Override
  protected Map<K,V> delegate(){
    return delegate;
  }

  @Override
  public ISet<K> keySet() {
    return new ISetImp<>(delegate().keySet());
  }

  @Override
  public ISet<Entry<K, V>> entrySet() {
    return new ISetImp<>(delegate().entrySet());
  }

}
