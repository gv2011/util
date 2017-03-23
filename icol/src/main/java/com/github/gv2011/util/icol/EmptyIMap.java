package com.github.gv2011.util.icol;

import java.util.AbstractMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiConsumer;

final class EmptyIMap<K,V> extends AbstractMap<K,V> implements ISortedMap<K,V>{

  @SuppressWarnings("rawtypes")
  private static final ISortedMap INSTANCE = new EmptyIMap();

  @SuppressWarnings("unchecked")
  static final <K,V> ISortedMap<K,V> instance(){return INSTANCE;}

  private EmptyIMap(){}



  @Override
  public Optional<V> tryGet(final Object key) {
    return Optional.empty();
  }

  @Override
  public final int size() {
    return 0;
  }

  @Override
  public final boolean isEmpty() {
    return true;
  }

  @Override
  public final int hashCode() {
    return 0;
  }

  @Override
  public final V getOrDefault(final Object key, final V defaultValue) {
    return defaultValue;
  }

  @Override
  public final Entry<K, V> single() {
    throw new NoSuchElementException();
  }

  @Override
  public final Entry<K, V> first() {
    throw new NoSuchElementException();
  }

  @Override
  public final Optional<Entry<K, V>> tryGetFirst() {
    return Optional.empty();
  }

  @Override
  public final boolean containsValue(final Object value) {
    return false;
  }

  @Override
  public final boolean containsKey(final Object key) {
    return false;
  }

  @Override
  public final V get(final Object key) {
    throw new NoSuchElementException();
  }

  @Override
  public final ISortedSet<K> keySet() {
    return EmptyISet.empty();
  }

  @Override
  public final ISortedSet<Entry<K, V>> entrySet() {
    return EmptyISet.empty();
  }

  @Override
  public final boolean equals(final Object o) {
    if(this==o) return true;
    else if(!(o instanceof Map)) return false;
    else return ((Map<?,?>)o).isEmpty();
   }

  @Override
  public final IList<V> values() {
    return EmptyIList.empty();
  }

  @Override
  public final K firstKey() {
    throw new NoSuchElementException();
  }

  @Override
  public final K lastKey() {
    throw new NoSuchElementException();
  }

  @Override
  public final ISortedMap<K, V> subMap(final K fromKey, final K toKey) {
    return this;
  }

  @Override
  public final ISortedMap<K, V> headMap(final K toKey) {
    return this;
  }

  @Override
  public final ISortedMap<K, V> tailMap(final K fromKey) {
    return this;
  }

  @Override
  public final void forEach(final BiConsumer<? super K, ? super V> action) {
  }

  @Override
  public Optional<Entry<K, V>> tryGetLowerEntry(final K key) {
    return Optional.empty();
  }

  @Override
  public Optional<K> tryGetLowerKey(final K key) {
    return Optional.empty();
  }

  @Override
  public Optional<Entry<K, V>> tryGetFloorEntry(final K key) {
    return Optional.empty();
  }

  @Override
  public Optional<K> tryGetFloorKey(final K key) {
    return Optional.empty();
  }

  @Override
  public Optional<Entry<K, V>> tryGetCeilingEntry(final K key) {
    return Optional.empty();
  }

  @Override
  public Optional<K> tryGetCeilingKey(final K key) {
    return Optional.empty();
  }

  @Override
  public Optional<Entry<K, V>> tryGetHigherEntry(final K key) {
    return Optional.empty();
  }

  @Override
  public Optional<K> tryGetHigherKey(final K key) {
    return Optional.empty();
  }

  @Override
  public Optional<Entry<K, V>> tryGetFirstEntry() {
    return Optional.empty();
  }

  @Override
  public Optional<Entry<K, V>> tryGetLastEntry() {
    return Optional.empty();
  }

  @Override
  public ISortedMap<K, V> descendingMap() {
    return this;
  }

  @Override
  public ISortedSet<K> navigableKeySet() {
    return EmptyISet.empty();
  }

  @Override
  public ISortedSet<K> descendingKeySet() {
    return EmptyISet.empty();
  }

  @Override
  public ISortedMap<K, V> subMap(final K fromKey, final boolean fromInclusive, final K toKey, final boolean toInclusive) {
    return this;
  }

  @Override
  public ISortedMap<K, V> headMap(final K toKey, final boolean inclusive) {
    return this;
  }

  @Override
  public ISortedMap<K, V> tailMap(final K fromKey, final boolean inclusive) {
    return this;
  }

}
