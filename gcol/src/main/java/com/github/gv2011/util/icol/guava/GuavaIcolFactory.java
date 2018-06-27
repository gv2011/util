package com.github.gv2011.util.icol.guava;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
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
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.gv2011.util.XStream;
import com.github.gv2011.util.icol.ICollectionFactory;
import com.github.gv2011.util.icol.IComparableList;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.icol.Path;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

final class GuavaIcolFactory implements ICollectionFactory{

  static final GuavaIcolFactory INSTANCE = new GuavaIcolFactory();

  private GuavaIcolFactory(){}

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final ISortedSet EMPTY_SORTED_SET = new ISortedSetWrapper(ImmutableSortedSet.of());

  @SuppressWarnings({ "unchecked", "rawtypes" })
  static final ISortedMap EMPTY_MAP = new ISortedMapWrapper(ImmutableSortedMap.of());

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Comparable<? super T>> ISortedSet<T> emptySortedSet() {
    return EMPTY_SORTED_SET;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <K, V> IMap<K, V> emptyMap() {
    return EMPTY_MAP;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <K extends Comparable<? super K>, V> ISortedMap<K, V> emptySortedMap() {
    return EMPTY_MAP;
  }

  @Override
  public <T> IList<T> listOf(final T element) {
    return new IListWrapper<>(ImmutableList.of(element));
  }

  @Override
  public <T extends Comparable<? super T>> ISortedSet<T> sortedSetOf(final T element) {
    return new ISortedSetWrapper<>(ImmutableSortedSet.of(element));
  }

  @Override
  public <K, V> IMap<K, V> mapOf(final K key, final V value) {
    return new IMapWrapper<>(ImmutableMap.of(key, value));
  }

  @Override
  public <K extends Comparable<? super K>, V> ISortedMap<K, V> sortedMapOf(final K key, final V value) {
    return new ISortedMapWrapper<>(ImmutableSortedMap.of(key, value));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <E> IList<E> listFrom(final Collection<? extends E> collection){
    if(collection.isEmpty()) return emptyList();
    else if(collection instanceof IListWrapper) return (IListWrapper<E>) collection;
    else if(collection.size()==1) return listOf(collection.iterator().next());
    else return this.<E>listBuilder().addAll(collection).build();
  }


  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public <E> ISet<E> setFrom(final Collection<? extends E> collection) {
    if(collection.isEmpty()) return emptySet();
    else if(collection instanceof ISetWrapper) return (ISetWrapper) collection;
    else if(collection.size()==1) return setOf(collection.iterator().next());
    else return this.<E>setBuilder().addAll(collection).build();
  }


  @SuppressWarnings("unchecked")
  @Override
  public <E extends Comparable<? super E>> ISortedSet<E> sortedSetFrom(final Collection<? extends E> collection) {
    if(collection.isEmpty()) return emptySortedSet();
    else if(collection instanceof ISortedSetWrapper) return (ISortedSetWrapper<E>) collection;
    else if(collection.size()==1) return sortedSetOf(collection.iterator().next());
    else return this.<E>sortedSetBuilder().addAll(collection).build();
  }


  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public <K, V> IMap<K, V> mapFrom(final Map<? extends K, ? extends V> map) {
    if(map.isEmpty()) return emptyMap();
    else if(map instanceof IMapWrapper) return (IMapWrapper) map;
    else if(map.size()==1){
      final Entry<? extends K, ? extends V> e = map.entrySet().iterator().next();
      return mapOf(e.getKey(), e.getValue());
    }
    else return this.<K,V>mapBuilder().putAll(map).build();
  }


  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public <K extends Comparable<? super K>, V> ISortedMap<K, V> sortedMapFrom(final Map<? extends K, ? extends V> map) {
    if(map.isEmpty()) return emptySortedMap();
    else if(map instanceof ISortedMapWrapper) return (ISortedMapWrapper) map;
    else if(map.size()==1){
      final Entry<? extends K, ? extends V> e = map.entrySet().iterator().next();
      return sortedMapOf(e.getKey(), e.getValue());
    }
    else return this.<K,V>sortedMapBuilder().putAll(map).build();
  }


  @Override
  public <E> IList.Builder<E> listBuilder() {
    return new IListBuilder<>();
  }

  @Override
  public <E extends Comparable<? super E>> IComparableList.Builder<E> comparableListBuilder() {
    return new IComparableListBuilder<>();
  }

  @Override
  public <E> ISet.Builder<E> setBuilder() {
    return new ISetBuilder<>();
  }

  @Override
  public <E extends Comparable<? super E>> ISortedSet.Builder<E> sortedSetBuilder() {
    return new ISortedSetBuilder<>();
  }

  @Override
  public <K, V> IMap.Builder<K, V> mapBuilder() {
    return new IMapBuilder<>();
  }

  @Override
  public <K extends Comparable<? super K>, V> ISortedMap.Builder<K, V> sortedMapBuilder() {
    return new ISortedMapBuilder<>();
  }

  @Override
  public <T> Collector<T, ?, IList<T>> listCollector() {
    return new IListCollector<>();
  }

  @Override
  public <T> Collector<T, ?, ISet<T>> setCollector() {
    return new ISetCollector<>();
  }

  @Override
  public <T extends Comparable<? super T>> Collector<T, ?, ISortedSet<T>> sortedSetCollector() {
    return new ISortedSetCollector<>();
  }

  @Override
  public <T, K, V> Collector<T, ?, IMap<K, V>> mapCollector(
    final Function<? super T, ? extends K> keyMapper,
    final Function<? super T, ? extends V> valueMapper
  ) {
    return new IMapCollector<>(keyMapper, valueMapper);
  }

  @Override
  public <T, K extends Comparable<? super K>, V> Collector<T, ?, ISortedMap<K, V>> sortedMapCollector(
    final Function<? super T, ? extends K> keyMapper,
    final Function<? super T, ? extends V> valueMapper
  ) {
    return new ISortedMapCollector<>(keyMapper, valueMapper);
  }

  @Override
  public Path emptyPath() {
    return PathImp.EMPTY;
  }

  @Override
  public <E> XStream<E> xStream(final Stream<E> s) {
    return XStreamImp.xStream(s);
  }

  @Override
  public <E> XStream<E> pStream(final Stream<E> s) {
    return XStreamImp.pStream(s);
  }

  @Override
  public <E> XStream<E> xStream(final Spliterator<E> spliterator, final boolean parallel) {
    return XStreamImp.xStream(StreamSupport.stream(spliterator, parallel));
  }


}
