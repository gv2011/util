package com.github.gv2011.util.icol2;

import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.util.Optional;

import com.github.gv2011.util.icol.ISortedMap;

abstract class AbstractSortedMap<K,V> extends AbstractMap<K,V> implements ISortedMap<K,V>{

  protected AbstractSortedMap() {
    super();
  }

  @Override
  public final Optional<java.util.Map.Entry<K, V>> tryGetLowerEntry(final K key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public final Optional<K> tryGetLowerKey(final K key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public final Optional<java.util.Map.Entry<K, V>> tryGetFloorEntry(final K key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public final Optional<K> tryGetFloorKey(final K key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public final Optional<K> tryGetLastKey() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public final Optional<java.util.Map.Entry<K, V>> tryGetCeilingEntry(final K key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public final Optional<K> tryGetCeilingKey(final K key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public final Optional<java.util.Map.Entry<K, V>> tryGetHigherEntry(final K key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public final Optional<K> tryGetHigherKey(final K key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public final Optional<java.util.Map.Entry<K, V>> tryGetFirstEntry() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public final Optional<java.util.Map.Entry<K, V>> tryGetLastEntry() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Optional<V> tryGet(final Object key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public final java.util.Map.Entry<K, V> single() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }
}
