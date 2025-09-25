package com.github.gv2011.util.gcol;

import static com.github.gv2011.util.num.NumUtils.doNTimes;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.function.BinaryOperator;
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
import com.github.gv2011.util.icol.ISetList;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.icol.Nothing;
import com.github.gv2011.util.icol.Path;
import com.github.gv2011.util.icol.Single;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

final class GuavaIcolFactory implements ICollectionFactory{

  static final GuavaIcolFactory INSTANCE = new GuavaIcolFactory();

  private GuavaIcolFactory(){}

  @Override
  public Nothing nothing() {
    return IEmpty.INSTANCE;
  }

  @Override
  public Nothing empty() {
     return IEmpty.INSTANCE;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E> ISetList<E> emptyList() {
    return IEmptyList.INSTANCE;
  }

  @Override
  public <E> Single<E> setOf(final E element) {
    return ISingle.of(element);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Comparable<? super T>> ISortedSet<T> emptySortedSet() {
    return ISortedSetWrapper.EMPTY;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <K, V> IMap<K, V> emptyMap() {
    return ISortedMapWrapper.EMPTY;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <K extends Comparable<? super K>, V> ISortedMap<K, V> emptySortedMap() {
    return ISortedMapWrapper.EMPTY;
  }

  @Override
  public <T> ISetList<T> listOf(final T element) {
    return new ISetListWrapper<>(ImmutableList.of(element));
  }

  @Override
  public <E> IList<E> filledList(final E element, final int size) {
    if(size==0) return emptyList();
    else{
      final Builder<Object> b = ImmutableList.builderWithExpectedSize(size);
      doNTimes(size, ()->b.add(element));
      return new IListWrapper<>(ImmutableList.of(element));
    }
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
  public <E> ISetList.Builder<E> setListBuilder() {
    return new ISetListBuilder<>();
  }

  @Override
  public Path.Builder pathBuilder() {
    return new PathCollector.PathBuilder();
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
  public <E> Collector<E, ?, ISetList<E>> setListCollector() {
    return new ISetListCollector<>();
  }

  @Override
  public <T> Collector<T, ?, ISet<T>> setCollector() {
    return new ISetCollector<>();
  }

  @Override
  public <T> Collector<T, ?, ISet<T>> transitiveClosure(final Function<T,Stream<T>> dependents) {
    return new TransitiveClosureCollector<>(dependents);
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
  public Collector<String, ?, Path> pathCollector() {
    return new PathCollector();
  }

  @Override
  public Path emptyPath() {
    return PathImp.EMPTY;
  }

  @Override
  public Path pathFrom(final Collection<String> collection) {
    return collection.isEmpty() ? PathImp.EMPTY : new PathImp(listFrom(collection));
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

  @Override
  public <K extends Comparable<? super K>, V> ISortedMap<K, V> priorityMerge(
      final IList<Stream<? extends V>> sources,
      final Function<? super V, ? extends K> key,
      final BinaryOperator<V> mergeFunction
    ) {
    return PriorityMerger.priorityMerge(sources, key, mergeFunction);
  }
}
