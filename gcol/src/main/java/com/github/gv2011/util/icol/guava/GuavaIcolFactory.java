package com.github.gv2011.util.icol.guava;

import java.util.function.Function;
import java.util.stream.Collector;

import com.github.gv2011.util.icol.ICollectionFactory;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

public final class GuavaIcolFactory implements ICollectionFactory{

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final IList EMPTY_LIST = new IListWrapper(ImmutableList.of());

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final ISortedSet EMPTY_SET = new ISortedSetWrapper(ImmutableSortedSet.of());

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static final ISortedMap EMPTY_MAP = new ISortedMapWrapper(ImmutableSortedMap.of());

  @SuppressWarnings("unchecked")
  @Override
  public <T> IList<T> emptyList() {
    return EMPTY_LIST;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> ISet<T> emptySet() {
    return EMPTY_SET;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Comparable<? super T>> ISortedSet<T> emptySortedSet() {
    return EMPTY_SET;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <K, V> IMap<K, V> emptyMap() {
    return EMPTY_MAP;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> IList<T> listOf(final T element, final T... more) {
    return ((IList.Builder<T>)listBuilder()).add(element).addAll(more).build();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> ISet<T> setOf(final T element, final T... more) {
    return ((ISet.Builder<T>)setBuilder()).add(element).addAll(more).build();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> ISortedSet<T> sortedSetOf(final T element, final T... more) {
    return ((ISortedSet.Builder<T>)sortedSetBuilder()).add(element).addAll(more).build();
  }

  @Override
  public <T> IList<T> listOf(final T element) {
    return new IListWrapper<>(ImmutableList.of(element));
  }

  @Override
  public <T> ISet<T> setOf(final T element) {
    return new ISetWrapper<>(ImmutableSet.of(element));
  }

  @SuppressWarnings("deprecation") //Suppress false Eclipse warning
  @Override
  public <T> ISortedSet<T> sortedSetOf(final T element) {
    return new ISortedSetWrapper<>(ImmutableSortedSet.of(element));
  }



  @Override
  public <T> IList<T> asList(final T[] elements) {
    return new IListWrapper<>(ImmutableList.copyOf(elements));
  }

  @Override
  public <T> ISet<T> asSet(final T[] elements) {
    return new ISetWrapper<>(ImmutableSet.copyOf(elements));
  }

  @Override
  public <K, V> IMap<K, V> mapOf(final K key, final V value) {
    return new IMapWrapper<>(ImmutableMap.of(key, value));
  }

  @Override
  public <E> IList.Builder<E> listBuilder() {
    return new IListBuilder<>();
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
  public <T> Collector<T, ?, ISet<T>> setCollector() {
    return new ISetCollector<>();
  }

  @Override
  public <T extends Comparable<?>> Collector<T, ?, ISortedSet<T>> sortedSetCollector() {
    return new ISortedSetCollector<>();
  }

  @Override
  public <T> Collector<T, ?, IList<T>> listCollector() {
    return new IListCollector<>();
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

}
