package com.github.gv2011.util.icol;

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.CollectionUtils.toIList;
import static com.github.gv2011.util.CollectionUtils.toISet;
import static com.github.gv2011.util.CollectionUtils.toISortedMap;
import static java.util.stream.Collectors.joining;

import java.util.Map;
import java.util.Optional;

import com.github.gv2011.util.Equal;

public abstract class AbstractISortedMap<K extends Comparable<? super K>, V> implements ISortedMap<K,V>{

  @Override
  public abstract ISortedSet<K> keySet();

  @Override
  public abstract Optional<V> tryGet(final Object key);

  @Override
  public ISet<Entry<K, V>> entrySet() {
    return keySet().stream()
      .map(k->(Entry<K, V>)pair(k, get(k)))
      .collect(toISet())
    ;
  }

  @Override
  public Entry<K, V> single() {
    return entrySet().single();
  }

  @Override
  public int size() {
    return keySet().size();
  }

  @Override
  public boolean isEmpty() {
    return keySet().isEmpty();
  }

  @Override
  public boolean containsKey(final Object key) {
    return keySet().contains(key);
  }

  @Override
  public boolean containsValue(final Object value) {
    return values().contains(value);
  }

  @Override
  public IList<V> values() {
    return keySet().stream().map(this::get).collect(toIList());
  }

  @Override
  public Optional<Entry<K, V>> tryGetLowerEntry(final K key) {
    return keySet().tryGetLower(key).map(k->(Entry<K,V>)pair(k, get(k)));
  }

  @Override
  public Optional<K> tryGetLowerKey(final K key) {
    return keySet().tryGetLower(key);
  }

  @Override
  public Optional<Entry<K, V>> tryGetFloorEntry(final K key) {
    return keySet().tryGetFloor(key).map(k->(Entry<K,V>)pair(k, get(k)));
  }

  @Override
  public Optional<K> tryGetFloorKey(final K key) {
    return keySet().tryGetFloor(key);
  }

  @Override
  public Optional<K> tryGetFirstKey() {
    return keySet().tryGetFirst();
  }

  @Override
  public Optional<K> tryGetLastKey() {
    return keySet().tryGetLast();
  }

  @Override
  public Optional<Entry<K, V>> tryGetCeilingEntry(final K key) {
    return keySet().tryGetCeiling(key).map(k->(Entry<K,V>)pair(k, get(k)));
  }

  @Override
  public Optional<K> tryGetCeilingKey(final K key) {
    return keySet().tryGetCeiling(key);
  }

  @Override
  public Optional<Entry<K, V>> tryGetHigherEntry(final K key) {
    return keySet().tryGetHigher(key).map(k->(Entry<K,V>)pair(k, get(k)));
  }

  @Override
  public Optional<K> tryGetHigherKey(final K key) {
    return keySet().tryGetHigher(key);
  }

  @Override
  public Optional<Entry<K, V>> tryGetFirstEntry() {
    return keySet().tryGetFirst().map(k->(Entry<K,V>)pair(k, get(k)));
  }

  @Override
  public Optional<Entry<K, V>> tryGetLastEntry() {
    return keySet().tryGetLast().map(k->(Entry<K,V>)pair(k, get(k)));
  }

  @Override
  public ISortedMap<K, V> descendingMap() {
    final ISortedSet<K> reversed = keySet().descendingSet();
    return new AbstractISortedMap<K, V>(){
      @Override
      public ISortedSet<K> keySet() {
        return reversed;
      }
      @Override
      public Optional<V> tryGet(final Object key) {
        return AbstractISortedMap.this.tryGet(key);
      }
    };
  }

  @Override
  public ISortedMap<K, V> subMap(
    final K fromKey, final boolean fromInclusive, final K toKey, final boolean toInclusive
  ) {
    return keySet().subSet(fromKey, fromInclusive, toKey, toInclusive).stream().collect(toISortedMap(
      k->k,
      this::get
    ));
  }

  @Override
  public int hashCode() {
    return entrySet().stream().mapToInt(Entry::hashCode).sum();
  }

  @Override
  public boolean equals(final Object obj) {
    return Equal.equal(this, obj, Map.class, m->{
      if(size()!=m.size()) return false;
      else if(!keySet().equals(m.keySet())) return false;
      else return keySet().stream().allMatch(k->get(k).equals(m.get(k)));
    });
  }

  @Override
  public String toString() {
    return keySet().stream().map(k->k+"="+get(k)).collect(joining(", ","{","}"));
  }


}