package com.github.gv2011.util.icol;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

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

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.ServiceLoaderUtils;
import com.github.gv2011.util.XStream;


/**
 * Convenience class with static methods that mirror the methods of the default {@link ICollectionFactory}.
 */
public final class ICollections {

  private ICollections(){staticClass();}

  private static final Constant<ICollectionFactory> ICOLF = Constants.softRefConstant(
      ()->ServiceLoaderUtils.loadService(ICollectionFactorySupplier.class).get()
  );

  public static final ICollectionFactory iCollections(){return ICOLF.get();}

  @SuppressWarnings("rawtypes")
  static final Opt EMPTY = iCollections().empty();

  @SuppressWarnings("rawtypes")
  static final IList EMPTY_LIST = iCollections().emptyList();


  //Empty:

  @SuppressWarnings("unchecked")
  public static <E> Opt<E> empty(){
    return EMPTY;
  }

  @SuppressWarnings("unchecked")
  public static <E> IList<E> emptyList(){
    return EMPTY_LIST;
  }

  @SuppressWarnings("unchecked")
  public static <E> Opt<E> emptySet(){
    return EMPTY;
  }

  public static <E extends Comparable<? super E>> ISortedSet<E> emptySortedSet() {
    return iCollections().emptySortedSet();
  }

  public static <K,V> IMap<K,V> emptyMap() {
    return iCollections().emptyMap();
  }

  public static <K extends Comparable<? super K>,V> ISortedMap<K,V> emptySortedMap() {
    return iCollections().emptySortedMap();
  }


  //Single:

  public static <E> IList<E> listOf(final E element) {
    return iCollections().listOf(element);
  }

  public static <E> Opt<E> single(final E element){
    return iCollections().setOf(element);
  }

  public static <E> Opt<E> setOf(final E element){
    return iCollections().single(element);
  }

  public static <E extends Comparable<? super E>> ISortedSet<E> sortedSetOf(final E element) {
    return iCollections().sortedSetOf(element);
  }

  public static <K,V> IMap<K,V> mapOf(final K key, final V value) {
    return iCollections().emptyMap();
  }

  public static <K extends Comparable<? super K>,V> ISortedMap<K,V> sortedMapOf(final K key, final V value) {
    return iCollections().sortedMapOf(key, value);
  }


  //Varargs:

  @SafeVarargs
  public static <E> IList<E> listOf(final E e1, final E e2, final E... more){
    return iCollections().listOf(e1, e2, more);
  }

  @SuppressWarnings("unchecked")
  public static <E> ISet<E> setOf(final E e1, final E e2, final E... more){
    return iCollections().setOf(e1, e2, more);
  }

  @SuppressWarnings("unchecked")
  public static <E extends Comparable<? super E>> ISortedSet<E> sortedSetOf(final E e1, final E e2, final E... more){
    return iCollections().sortedSetOf(e1, e2, more);
  }


  //Optional:

  public static <E> Opt<E> ofOptional(final Optional<? extends E> optional){
    return iCollections().ofOptional(optional);
  }

  public static <E> IList<E> listFrom(final Optional<? extends E> optional){
    return iCollections().listFrom(optional);
  }


  //Collections:

  public static <E> IList<E> listFrom(final Collection<? extends E> collection){
    return iCollections().listFrom(collection);
  }

  public static <E> ISet<E> setFrom(final Collection<? extends E> collection){
    return iCollections().setFrom(collection);
  }

  public static <E extends Comparable<? super E>> ISortedSet<E> sortedSetFrom(
    final Collection<? extends E> collection
  ){
    return iCollections().sortedSetFrom(collection);
  }

  public static <K,V> IMap<K,V> mapFrom(final Map<? extends K,? extends V> map){
    return iCollections().mapFrom(map);
  }

  public static <K extends Comparable<? super K>,V> ISortedMap<K,V> sortedMapFrom(
    final Map<? extends K,? extends V> map
  ){
    return iCollections().sortedMapFrom(map);
  }


  //Arrays:

  public static <E> IList<E> asList(final E[] elements){
    return iCollections().asList(elements);
  }

  public static <E> ISet<E> asSet(final E[] elements){
    return iCollections().asSet(elements);
  }

  public static <E extends Comparable<? super E>> ISortedSet<E> asSortedSet(final E[] elements){
    return iCollections().asSortedSet(elements);
  }


  //Builders:

  public static <E> IList.Builder<E> listBuilder() {
    return iCollections().listBuilder();
  }

  public static <E extends Comparable<? super E>> IComparableList.Builder<E> comparableListBuilder() {
    return iCollections().comparableListBuilder();
  }

  public static <E> ISet.Builder<E> setBuilder() {
    return iCollections().setBuilder();
  }

  public static <E extends Comparable<? super E>> ISortedSet.Builder<E> sortedSetBuilder() {
    return iCollections().sortedSetBuilder();
  }

  public static <K,V> IMap.Builder<K,V> mapBuilder() {
    return iCollections().mapBuilder();
  }

  public static <K extends Comparable<? super K>,V> ISortedMap.Builder<K,V> sortedMapBuilder() {
    return iCollections().sortedMapBuilder();
  }


  //Collectors:

  public static <E> Collector<E, ?, IList<E>> toIList() {
    return iCollections().listCollector();
  }

  public static <E> Collector<E, ?, ISet<E>> toISet() {
    return iCollections().setCollector();
  }

  public static <E extends Comparable<? super E>> Collector<E, ?, ISortedSet<E>> toISortedSet() {
    return iCollections().sortedSetCollector();
  }

  public static <E, K, V>
  Collector<E, ?, IMap<K,V>> toIMap(
    final Function<? super E, ? extends K> keyMapper,
    final Function<? super E, ? extends V> valueMapper
  ){
    return iCollections().mapCollector(keyMapper, valueMapper);
  }

  public static <E extends Entry<? extends K, ? extends V>, K, V>
  Collector<E, ?, IMap<K,V>> toIMap(){
    return iCollections().mapCollector();
  }

  public static <E, K extends Comparable<? super K>, V>
  Collector<E, ?, ISortedMap<K,V>> toISortedMap(
    final Function<? super E, ? extends K> keyMapper,
    final Function<? super E, ? extends V> valueMapper
  ){
    return iCollections().sortedMapCollector(keyMapper, valueMapper);
  }

  public static <K extends Comparable<? super K>, V>
  Collector<Entry<? extends K, ? extends V>, ?, ISortedMap<K,V>> toISortedMap(){
    return iCollections().sortedMapCollector();
  }


  //Upcast:

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static final <U,E extends U> Opt<U> upcast(final Opt<E> optional){
    return (Opt)optional;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static final <U,E extends U> IList<U> upcast(final IList<E> list){
    return (IList)list;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static final <U,E extends U> ISet<U> upcast(final ISet<E> set){
    return (ISortedSet)set;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static final <U extends Comparable<? super U>,E extends U> ISortedSet<U> upcast(final ISortedSet<E> set){
    return (ISortedSet)set;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static final <UK,K extends UK,UV, V extends UV> IMap<UK,UV> upcast(final IMap<K,V> map){
    return (IMap)map;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static final <UK extends Comparable<? super UK>,K extends UK,UV, V extends UV> ISortedMap<UK,UV>
    upcast(final ISortedMap<K,V> map
  ){
    return (ISortedMap)map;
  }

  //Other:

  public static Path emptyPath() {
    return iCollections().emptyPath();
  }

  public static <E> XStream<E> xStream(final Stream<E> s) {
    return iCollections().xStream(s);
  }

  public static <E> XStream<E> pStream(final Stream<E> s) {
    return iCollections().pStream(s);
  }

  public static <E> XStream<E> xStream(final Spliterator<E> spliterator, final boolean parallel) {
    return iCollections().xStream(spliterator, parallel);
  }
  
  public static <E> ISet<E> intersection(final ICollection<E> first, final Collection<?> second) {
    return first.parallelStream().filter(second::contains).collect(toISet());
  }

  public static <C extends Comparable<? super C>> ISortedSet<C> sortedIntersection(
    final ICollection<C> first, final Collection<?> second
  ) {
    return first.parallelStream().filter(second::contains).collect(toISortedSet());
  }

}
