package com.github.gv2011.util.icol;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;


public interface ICollectionFactory {

  <T> IList<T> emptyList();

  <T> ISet<T> emptySet();

  <T extends Comparable<? super T>> ISortedSet<T> emptySortedSet();

  <K,V> IMap<K,V> emptyMap();

  <K extends Comparable<? super K>,V> ISortedMap<K,V> emptySortedMap();

  <T> IList<T> listOf(final T element);

  <T> IList<T> listFrom(final Collection<? extends T> collection);

  @SuppressWarnings("unchecked")
  <T> IList<T> listOf(final T element, final T... more);

  <T> ISet<T> setOf(final T element);

  @SuppressWarnings("unchecked")
  <T> ISet<T> setOf(final T element, final T... more);

  <T> ISortedSet<T> sortedSetOf(final T element);

  @SuppressWarnings("unchecked")
  <T> ISortedSet<T> sortedSetOf(final T element, final T... more);

  <T> ISortedSet<T> sortedSetFrom(Collection<? extends T> elements);

  <T> IList<T> asList(T[] elements);
  <T> ISet<T> asSet(T[] elements);

  <K,V> IMap<K,V> mapOf(final K key, V value);

  default <T> IList<T> ofOptional(final Optional<? extends T> optional){
    return optional.map(e->listOf((T)e)).orElse(emptyList());
  }

  <E> IList.Builder<E> listBuilder();


  <E> ISet.Builder<E> setBuilder();

  <E extends Comparable<? super E>> ISortedSet.Builder<E> sortedSetBuilder();


  <K,V> IMap.Builder<K,V> mapBuilder();

  <K extends Comparable<? super K>,V> ISortedMap.Builder<K,V> sortedMapBuilder();


  @SuppressWarnings("unchecked")
  default <E> IList<E> upcast(final IList<? extends E> list){return (IList<E>) list;}

  @SuppressWarnings("unchecked")
  default <E> ISet<E> upcast(final ISet<? extends E> set){return (ISet<E>) set;}

  @SuppressWarnings("unchecked")
  default <E> ISortedSet<E> upcast(final ISortedSet<? extends E> set){return (ISortedSet<E>) set;}

  @SuppressWarnings("unchecked")
  default <K,V> IMap<K,V> upcast(final IMap<? extends K,? extends V> map){return (IMap<K,V>) map;}

  @SuppressWarnings("unchecked")
  default <K,V> ISortedMap<K,V> upcast(final ISortedMap<? extends K,? extends V> map){return (ISortedMap<K,V>) map;}

  <T> Collector<T, ?, ISet<T>> setCollector();

  <T extends Comparable<?>> Collector<T, ?, ISortedSet<T>> sortedSetCollector();

  <T> Collector<T, ?, IList<T>> listCollector();

  <T, K, V>
  Collector<T, ?, IMap<K,V>> mapCollector(
    Function<? super T, ? extends K> keyMapper,
    Function<? super T, ? extends V> valueMapper
  );

  default <E extends Entry<? extends K, ? extends V>, K, V>
  Collector<E, ?, IMap<K,V>> mapCollector(){
    return mapCollector(Entry::getKey, Entry::getValue);
  }

  <T, K extends Comparable<? super K>, V>
  Collector<T, ?, ISortedMap<K,V>> sortedMapCollector(
    Function<? super T, ? extends K> keyMapper,
    Function<? super T, ? extends V> valueMapper
  );

  default <K extends Comparable<? super K>, V>
  Collector<Entry<? extends K, ? extends V>, ?, ISortedMap<K,V>> sortedMapCollector(){
    return sortedMapCollector(Entry::getKey, Entry::getValue);
  }

  default <K extends Comparable<? super K>, V> ISortedMap<K,V> copyOf(final Map<K, V> map){
    return map.entrySet().stream().collect(sortedMapCollector());
  }

  default <E> ISet<E> setOf(final Collection<E> collection){
    return this.<E>setBuilder().addAll(collection).build();
  }

}
