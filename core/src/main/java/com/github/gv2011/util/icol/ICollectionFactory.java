package com.github.gv2011.util.icol;

import static com.github.gv2011.util.CollectionUtils.stream;

import java.util.Arrays;

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

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.gv2011.util.XStream;


public interface ICollectionFactory {


  //Empty:

  @SuppressWarnings("unchecked")
  default <E> Opt<E> empty(){
    return IEmpty.INSTANCE;
  }

  @SuppressWarnings("unchecked")
  default <E> IList<E> emptyList(){
    return IEmptyList.INSTANCE;
  }

  @SuppressWarnings("unchecked")
  default <E> Opt<E> emptySet(){
    return IEmpty.INSTANCE;
  }

  <E extends Comparable<? super E>> ISortedSet<E> emptySortedSet();

  <K,V> IMap<K,V> emptyMap();

  <K extends Comparable<? super K>,V> ISortedMap<K,V> emptySortedMap();


  //Single:

  <E> IList<E> listOf(final E element);

  default <E> Opt<E> single(final E element){
    return setOf(element);
  }

  default <E> Opt<E> setOf(final E element){
    return new Single<>(element);
  }

  <E extends Comparable<? super E>> ISortedSet<E> sortedSetOf(final E element);

  <K,V> IMap<K,V> mapOf(final K key, V value);

  <K extends Comparable<? super K>,V> ISortedMap<K,V> sortedMapOf(final K key, V value);


  //Varargs:

  @SuppressWarnings("unchecked")
  default <E> IList<E> listOf(final E e0, final E e1, final E... more){
    return Stream.concat(Stream.of(e0,e1), Arrays.stream(more)).collect(listCollector());
  }

  @SuppressWarnings("unchecked")
  default <E> ISet<E> setOf(final E e0, final E e1, final E... more){
    return
      Stream.concat(
        Stream.of(e0, e1),
        StreamSupport.stream(Arrays.spliterator(more, 0, more.length), true)
      )
      .collect(setCollector())
    ;
  }

  @SuppressWarnings("unchecked")
  default <E extends Comparable<? super E>> ISortedSet<E> sortedSetOf(final E e0, final E e1, final E... more){
    return
      Stream.concat(
        Stream.of(e0, e1),
        StreamSupport.stream(Arrays.spliterator(more, 0, more.length), true)
      )
      .collect(sortedSetCollector())
    ;
  }


  //Optional:

  default <E> Opt<E> ofOptional(final Optional<? extends E> optional){
    return optional.map(e->ICollections.single((E)e)).orElse(empty());
  }

  default <E> IList<E> listFrom(final Optional<? extends E> optional){
    return optional.map(e->listOf((E)e)).orElse(emptyList());
  }


  //Collections:

  default <E> IList<E> listFrom(final Collection<? extends E> collection){
    if(collection.isEmpty()) return emptyList();
    else if(collection.size()==1) return listOf(collection.iterator().next());
    else return collection.stream().collect(listCollector());
  }

  default <E> ISet<E> setFrom(final Collection<? extends E> collection){
    if(collection.isEmpty()) return emptySet();
    else if(collection.size()==1) return setOf(collection.iterator().next());
    else return collection.parallelStream().collect(setCollector());
  }

  default <E extends Comparable<? super E>> ISortedSet<E> sortedSetFrom(final Collection<? extends E> collection){
    if(collection.isEmpty()) return emptySortedSet();
    else if(collection.size()==1) return sortedSetOf(collection.iterator().next());
    else return collection.parallelStream().collect(sortedSetCollector());
  }

  default <K,V> IMap<K,V> mapFrom(final Map<? extends K,? extends V> map){
    if(map.isEmpty()) return emptyMap();
    else if(map.size()==1){
      final Entry<? extends K, ? extends V> entry = map.entrySet().iterator().next();
      return mapOf(entry.getKey(), entry.getValue());
    }
    else return map.entrySet().parallelStream().collect(mapCollector());
  }

  default <K extends Comparable<? super K>,V> ISortedMap<K,V> sortedMapFrom(final Map<? extends K,? extends V> map){
    if(map.isEmpty()) return emptySortedMap();
    else if(map.size()==1){
      final Entry<? extends K, ? extends V> entry = map.entrySet().iterator().next();
      return sortedMapOf(entry.getKey(), entry.getValue());
    }
    else return map.entrySet().parallelStream().collect(sortedMapCollector());
  }


  //Arrays:

  default <E> IList<E> asList(final E[] elements){
    if(elements.length==0) return emptyList();
    else if(elements.length==1) return listOf(elements[0]);
    else return stream(elements).collect(listCollector());
  }

  default <E> ISet<E> asSet(final E[] elements){
    if(elements.length==0) return emptySet();
    else if(elements.length==1) return setOf(elements[0]);
    else return stream(elements).parallel().collect(setCollector());
  }

  default <E extends Comparable<? super E>> ISortedSet<E> asSortedSet(final E[] elements){
    if(elements.length==0) return emptySortedSet();
    else if(elements.length==1) return sortedSetOf(elements[0]);
    else return stream(elements).parallel().collect(sortedSetCollector());
  }


  //Builders:

  <E> IList.Builder<E> listBuilder();

  <E extends Comparable<? super E>> IComparableList.Builder<E> comparableListBuilder();

  <E> ISet.Builder<E> setBuilder();

  <E extends Comparable<? super E>> ISortedSet.Builder<E> sortedSetBuilder();

  <K,V> IMap.Builder<K,V> mapBuilder();

  <K extends Comparable<? super K>,V> ISortedMap.Builder<K,V> sortedMapBuilder();


  //Collectors:

  <E> Collector<E, ?, IList<E>> listCollector();

  <E> Collector<E, ?, ISet<E>> setCollector();

  <E extends Comparable<? super E>> Collector<E, ?, ISortedSet<E>> sortedSetCollector();

  <E, K, V>
  Collector<E, ?, IMap<K,V>> mapCollector(
    Function<? super E, ? extends K> keyMapper,
    Function<? super E, ? extends V> valueMapper
  );

  default <E extends Entry<? extends K, ? extends V>, K, V>
  Collector<E, ?, IMap<K,V>> mapCollector(){
    return mapCollector(Entry::getKey, Entry::getValue);
  }

  <E, K extends Comparable<? super K>, V>
  Collector<E, ?, ISortedMap<K,V>> sortedMapCollector(
    Function<? super E, ? extends K> keyMapper,
    Function<? super E, ? extends V> valueMapper
  );

  default <K extends Comparable<? super K>, V>
  Collector<Entry<? extends K, ? extends V>, ?, ISortedMap<K,V>> sortedMapCollector(){
    return sortedMapCollector(Entry::getKey, Entry::getValue);
  }


  //Other:

  Path emptyPath();

  <E> XStream<E> xStream(Stream<E> s);

  <E> XStream<E> xStream(Spliterator<E> spliterator, boolean parallel);
}
