package com.github.gv2011.util.icol.guava;

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
  public <K, V> IMap<K, V> emptyMap() {
    return EMPTY_MAP;
  }

  @Override
  public <T> IList<T> listOf(final T element) {
    return new IListWrapper<>(ImmutableList.of(element));
  }

  @Override
  public <T> ISet<T> setOf(final T element) {
    return new ISetWrapper<>(ImmutableSet.of(element));
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

}
