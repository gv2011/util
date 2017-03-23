package com.github.gv2011.util.icol;

import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.SortedSet;

final class ISortedMapImp<K,V> implements ISortedMap<K,V>{

  @Override
  public IList<V> values() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public ISet<java.util.Map.Entry<K, V>> entrySet() {
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
  public int size() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public boolean isEmpty() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public boolean containsKey(final Object key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public boolean containsValue(final Object value) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public V put(final K key, final V value) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public V remove(final Object key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public void putAll(final Map<? extends K, ? extends V> m) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public void clear() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public K firstKey() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public K lastKey() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public ISortedSet<K> keySet() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Optional<java.util.Map.Entry<K, V>> tryGetLowerEntry(final K key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Optional<K> tryGetLowerKey(final K key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Optional<java.util.Map.Entry<K, V>> tryGetFloorEntry(final K key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Optional<K> tryGetFloorKey(final K key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Optional<java.util.Map.Entry<K, V>> tryGetCeilingEntry(final K key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Optional<K> tryGetCeilingKey(final K key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Optional<java.util.Map.Entry<K, V>> tryGetHigherEntry(final K key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Optional<K> tryGetHigherKey(final K key) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Optional<java.util.Map.Entry<K, V>> tryGetFirstEntry() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Optional<java.util.Map.Entry<K, V>> tryGetLastEntry() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public ISortedMap<K, V> descendingMap() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public ISortedMap<K, V> subMap(final K fromKey, final boolean fromInclusive, final K toKey, final boolean toInclusive) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

//  static <K,V> ISortedMap<K,V> fromDelegate(final NavigableMap<K,V> delegate) {
//    final int size = delegate.size();
//    if(size>1) return new ISortedMapImp<>(delegate);
//    else if(size==1){
//      final K key = delegate.firstKey();
//      return new SingleIMap<>(key, delegate.get(key));
//    }
//    else return EmptyIMap.instance();
//  }

}
