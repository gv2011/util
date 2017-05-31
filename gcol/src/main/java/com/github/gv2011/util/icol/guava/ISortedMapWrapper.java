package com.github.gv2011.util.icol.guava;

import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.google.common.collect.ImmutableSortedMap;

final class ISortedMapWrapper<K,V> extends IMapWrapper<K,V,NavigableMap<K,V>> implements ISortedMap<K,V>{

  ISortedMapWrapper(final NavigableMap<K, V> delegate) {
    super(delegate);
  }

  private <T> T notNull(final T arg){
    if(arg==null) throw new NoSuchElementException();
    return arg;
  }

  @Override
  public Entry<K, V> lowerEntry(final K key) {
    return notNull(delegate.lowerEntry(key));
  }

  @Override
  public K lowerKey(final K key) {
    return notNull(delegate.lowerKey(key));
  }

  @Override
  public Entry<K, V> floorEntry(final K key) {
    return notNull(delegate.floorEntry(key));
  }

  @Override
  public K floorKey(final K key) {
    return notNull(delegate.floorKey(key));
  }

  @Override
  public Entry<K, V> ceilingEntry(final K key) {
    return notNull(delegate.ceilingEntry(key));
  }

  @Override
  public K ceilingKey(final K key) {
    return notNull(delegate.ceilingKey(key));
  }

  @Override
  public Entry<K, V> higherEntry(final K key) {
    return notNull(delegate.higherEntry(key));
  }

  @Override
  public K higherKey(final K key) {
    return notNull(delegate.higherKey(key));
  }

  @Override
  public Entry<K, V> firstEntry() {
    return notNull(delegate.firstEntry());
  }

  @Override
  public Entry<K, V> lastEntry() {
    return notNull(delegate.lastEntry());
  }

  @Override
  public ISortedMap<K, V> descendingMap() {
    return new ISortedMapWrapper<>(delegate.descendingMap());
  }

  @Override
  public K firstKey() {
    return delegate.firstKey();
  }

  @Override
  public ISortedSet<K> navigableKeySet() {
    return new ISortedSetWrapper<>(delegate.navigableKeySet());
  }

  @Override
  public K lastKey() {
    return delegate.lastKey();
  }

  @Override
  public ISortedSet<K> keySet() {
    return navigableKeySet();
  }

  @Override
  public ISortedSet<K> descendingKeySet() {
    return new ISortedSetWrapper<>(delegate.descendingKeySet());
  }

  @Override
  public ISortedMap<K, V> subMap(
    final K fromKey, final boolean fromInclusive, final K toKey, final boolean toInclusive
  ) {
    return new ISortedMapWrapper<>(delegate.subMap(fromKey, fromInclusive, toKey, toInclusive));
  }

  @Override
  public ISet<Entry<K, V>> entrySet() {
    return new ISetWrapper<>(delegate.entrySet());
  }

  @Override
  public ISortedMap<K, V> headMap(final K toKey, final boolean inclusive) {
    return new ISortedMapWrapper<>(delegate.headMap(toKey, inclusive));
  }

  @Override
  public ISortedMap<K, V> tailMap(final K fromKey, final boolean inclusive) {
    return new ISortedMapWrapper<>(delegate.tailMap(fromKey, inclusive));
  }

  @Override
  public ISortedMap<K, V> subMap(final K fromKey, final K toKey) {
    return new ISortedMapWrapper<>(delegate.subMap(fromKey, true, toKey, false));
  }

  @Override
  public ISortedMap<K, V> headMap(final K toKey) {
    return new ISortedMapWrapper<>(ImmutableSortedMap.copyOf(delegate.headMap(toKey)));
  }

  @Override
  public ISortedMap<K, V> tailMap(final K fromKey) {
    return new ISortedMapWrapper<>(ImmutableSortedMap.copyOf(delegate.tailMap(fromKey)));
  }

  @Override
  public Optional<Entry<K, V>> tryGetLowerEntry(final K key) {
    return Optional.ofNullable(delegate.lowerEntry(key));
  }

  @Override
  public Optional<K> tryGetLowerKey(final K key) {
    return Optional.ofNullable(delegate.lowerKey(key));
  }

  @Override
  public Optional<Entry<K, V>> tryGetFloorEntry(final K key) {
    return Optional.ofNullable(delegate.floorEntry(key));
  }

  @Override
  public Optional<K> tryGetFloorKey(final K key) {
    return Optional.ofNullable(delegate.floorKey(key));
  }

  @Override
  public Optional<K> tryGetFirstKey() {
    return isEmpty()?Optional.empty():Optional.of(delegate.firstKey());
  }

  @Override
  public Optional<K> tryGetLastKey() {
    return isEmpty()?Optional.empty():Optional.of(delegate.lastKey());
  }

  @Override
  public Optional<Entry<K, V>> tryGetCeilingEntry(final K key) {
    return Optional.ofNullable(delegate.ceilingEntry(key));
  }

  @Override
  public Optional<K> tryGetCeilingKey(final K key) {
    return Optional.ofNullable(delegate.ceilingKey(key));
  }

  @Override
  public Optional<Entry<K, V>> tryGetHigherEntry(final K key) {
    return Optional.ofNullable(delegate.higherEntry(key));
  }

  @Override
  public Optional<K> tryGetHigherKey(final K key) {
    return Optional.ofNullable(delegate.higherKey(key));
  }

  @Override
  public Optional<Entry<K, V>> tryGetFirstEntry() {
    return Optional.ofNullable(delegate.firstEntry());
  }

  @Override
  public Optional<Entry<K, V>> tryGetLastEntry() {
    return Optional.ofNullable(delegate.lastEntry());
  }

  @Override
  public String toString() {
    return delegate.toString();
  }


}
