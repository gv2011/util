package com.github.gv2011.util.icol;

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




import java.util.Comparator;
import java.util.NavigableMap;
import java.util.Optional;

public interface ISortedMap<K,V> extends IMap<K,V>, NavigableMap<K,V>{

  public static interface Builder<K,V> extends MapBuilder<ISortedMap<K,V>,K,V,Builder<K,V>>{}

  @Deprecated
  @Override
  default Comparator<? super K> comparator() {
    throw new UnsupportedOperationException();
  }

  @Override
  ISortedSet<K> keySet();

  @Override
  IList<V> values();

  @Override
  default Entry<K, V> lowerEntry(final K key) {
    return tryGetLowerEntry(key).get();
  }

  Optional<Entry<K, V>> tryGetLowerEntry(final K key);

  @Override
  default K lowerKey(final K key) {
    return tryGetLowerKey(key).get();
  }

  Optional<K> tryGetLowerKey(final K key);

  @Override
  default Entry<K, V> floorEntry(final K key) {
    return tryGetFloorEntry(key).get();
  }

  Optional<Entry<K, V>> tryGetFloorEntry(final K key);

  @Override
  default K floorKey(final K key) {
    return tryGetFloorKey(key).get();
  }

  Optional<K> tryGetFloorKey(final K key);

  @Override
  default K firstKey(){
    return tryGetFirstKey().get();
  }
  Optional<K> tryGetFirstKey();

  @Override
  default K lastKey(){
    return tryGetLastKey().get();
  }
  Optional<K> tryGetLastKey();

  @Override
  default Entry<K, V> ceilingEntry(final K key) {
    return tryGetCeilingEntry(key).get();
  }

  Optional<Entry<K, V>> tryGetCeilingEntry(final K key);

  @Override
  default K ceilingKey(final K key) {
    return tryGetCeilingKey(key).get();
  }

  Optional<K> tryGetCeilingKey(final K key);

  @Override
  default Entry<K, V> higherEntry(final K key) {
    return tryGetHigherEntry(key).get();
  }

  Optional<Entry<K, V>> tryGetHigherEntry(final K key);

  @Override
  default K higherKey(final K key) {
    return tryGetHigherKey(key).get();
  }

  Optional<K> tryGetHigherKey(final K key);

  @Override
  default Entry<K, V> firstEntry() {
    return tryGetFirstEntry().get();
  }

  Optional<Entry<K, V>> tryGetFirstEntry();

  @Override
  default Entry<K, V> lastEntry() {
    return tryGetLastEntry().get();
  }

  Optional<Entry<K, V>> tryGetLastEntry();

  @Deprecated
  @Override
  default Entry<K, V> pollFirstEntry() {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  default Entry<K, V> pollLastEntry() {
    throw new UnsupportedOperationException();
  }

  @Override
  ISortedMap<K, V> descendingMap();

  @Override
  default ISortedSet<K> navigableKeySet(){
    return keySet();
  }

  @Override
  default ISortedSet<K> descendingKeySet(){
    return descendingMap().keySet();
  }

  @Override
  default ISortedMap<K, V> subMap(final K fromKey, final K toKey){
    return subMap(fromKey, true, toKey, false);
  }

  @Override
  ISortedMap<K, V> subMap(final K fromKey, final boolean fromInclusive, final K toKey, final boolean toInclusive);

  @Override
  default ISortedMap<K, V> headMap(final K toKey){
    return headMap(toKey, false);
  }

  @Override
  default ISortedMap<K, V> headMap(final K toKey, final boolean inclusive){
    if(isEmpty()) return this;
    else return subMap(firstKey(), true, toKey, inclusive);
  }

  @Override
  default ISortedMap<K, V> tailMap(final K fromKey){
    return tailMap(fromKey, true);
  }

  @Override
  default ISortedMap<K, V> tailMap(final K fromKey, final boolean inclusive){
    if(isEmpty()) return this;
    else return subMap(fromKey, inclusive, lastKey(), true);
  }

}
