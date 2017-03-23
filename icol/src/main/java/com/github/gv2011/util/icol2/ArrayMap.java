package com.github.gv2011.util.icol2;

import java.util.Optional;

import com.github.gv2011.util.icol.ICollection;
import com.github.gv2011.util.icol.ISet;

public class ArrayMap<K,V> extends AbstractMap<K,V>{

  @Override
  public ISet<K> keySet() {
    return new ArraySet<>(keys);
  }

  @Override
  public ICollection<V> values() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Optional<V> tryGet(final Object key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public java.util.Map.Entry<K, V> single() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public ISet<java.util.Map.Entry<K, V>> entrySet() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

}
