package com.github.gv2011.util.icol.guava;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.util.NavigableMap;
import java.util.NoSuchElementException;

import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.icol.Opt;
import com.google.common.collect.ImmutableSortedMap;

final class ISortedMapWrapper<K extends Comparable<? super K>,V>
extends IMapWrapper<K,V,NavigableMap<K,V>>
implements ISortedMap<K,V>{

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
  public Opt<Entry<K, V>> tryGetLowerEntry(final K key) {
    return Opt.ofNullable(delegate.lowerEntry(key));
  }

  @Override
  public Opt<K> tryGetLowerKey(final K key) {
    return Opt.ofNullable(delegate.lowerKey(key));
  }

  @Override
  public Opt<Entry<K, V>> tryGetFloorEntry(final K key) {
    return Opt.ofNullable(delegate.floorEntry(key));
  }

  @Override
  public Opt<K> tryGetFloorKey(final K key) {
    return Opt.ofNullable(delegate.floorKey(key));
  }

  @Override
  public Opt<K> tryGetFirstKey() {
    return isEmpty()?Opt.empty():Opt.of(delegate.firstKey());
  }

  @Override
  public Opt<K> tryGetLastKey() {
    return isEmpty()?Opt.empty():Opt.of(delegate.lastKey());
  }

  @Override
  public Opt<Entry<K, V>> tryGetCeilingEntry(final K key) {
    return Opt.ofNullable(delegate.ceilingEntry(key));
  }

  @Override
  public Opt<K> tryGetCeilingKey(final K key) {
    return Opt.ofNullable(delegate.ceilingKey(key));
  }

  @Override
  public Opt<Entry<K, V>> tryGetHigherEntry(final K key) {
    return Opt.ofNullable(delegate.higherEntry(key));
  }

  @Override
  public Opt<K> tryGetHigherKey(final K key) {
    return Opt.ofNullable(delegate.higherKey(key));
  }

  @Override
  public Opt<Entry<K, V>> tryGetFirstEntry() {
    return Opt.ofNullable(delegate.firstEntry());
  }

  @Override
  public Opt<Entry<K, V>> tryGetLastEntry() {
    return Opt.ofNullable(delegate.lastEntry());
  }

  @Override
  public String toString() {
    return delegate.toString();
  }


}
