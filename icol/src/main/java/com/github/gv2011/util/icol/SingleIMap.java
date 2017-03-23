package com.github.gv2011.util.icol;

import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import com.github.gv2011.util.Pair;

public class SingleIMap<K,V> extends AbstractMap<K,V> implements ISortedMap<K,V>{

  private final K key;
  private final V value;

  SingleIMap(final K key, final V value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public V getOrDefault(final Object key, final V defaultValue) {
    return this.key.equals(key) ? value : defaultValue;
  }

  @Override
  public void forEach(final BiConsumer<? super K, ? super V> action) {
    action.accept(key, value);
  }

  @Override
  public Entry<K, V> single() {
    return new Pair<>(key, value);
  }

  @Override
  public Entry<K, V> first() {
    return single();
  }

  @Override
  public Optional<Entry<K, V>> tryGetFirst() {
    return Optional.of(single());
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public boolean containsValue(final Object value) {
    return this.value.equals(value);
  }

  @Override
  public boolean containsKey(final Object key) {
    return this.key.equals(key);
  }

  @Override
  public V get(final Object key) {
    if(!this.key.equals(key)) throw new NoSuchElementException();
    else return value;
  }

  @Override
  public ISet<K> keySet() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Collection<V> values() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public boolean equals(final Object o) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public int hashCode() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public IList<V> values() {
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
  public ISortedMap<K, V> subMap(final K fromKey, final K toKey) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public ISortedMap<K, V> headMap(final K toKey) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public ISortedMap<K, V> tailMap(final K fromKey) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public ISortedSet<K> keySet() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public ISortedSet<java.util.Map.Entry<K, V>> entrySet() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

}
