package com.github.gv2011.util.beans.imp;

/*-
 * #%L
 * util-beans
 * %%
 * Copyright (C) 2017 - 2018 Vinz (https://github.com/gv2011)
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

import static com.github.gv2011.util.CollectionUtils.atMostOne;
import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.CollectionUtils.toOptional;
import static com.github.gv2011.util.ex.Exceptions.bug;
import static com.github.gv2011.util.icol.ICollections.emptyList;
import static com.github.gv2011.util.icol.ICollections.emptyMap;
import static com.github.gv2011.util.icol.ICollections.emptySet;
import static com.github.gv2011.util.icol.ICollections.emptySortedMap;
import static com.github.gv2011.util.icol.ICollections.emptySortedSet;
import static com.github.gv2011.util.icol.ICollections.listFrom;
import static com.github.gv2011.util.icol.ICollections.setFrom;
import static com.github.gv2011.util.icol.ICollections.sortedSetFrom;
import static com.github.gv2011.util.icol.ICollections.toIList;
import static com.github.gv2011.util.icol.ICollections.toIMap;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static com.github.gv2011.util.icol.ICollections.toISortedMap;
import static com.github.gv2011.util.icol.ICollections.toISortedSet;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Stream;

import com.github.gv2011.util.Nothing;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;
import com.github.gv2011.util.json.JsonNull;

@SuppressWarnings({ "rawtypes", "unchecked" })
abstract class Structure<C,K,E> {

  private static final Structure OPT = new OptStructure();

  private static final Structure OPTIONAL = new OptionalStructure();

  private static final Structure LIST = new ListStructure();

  private static final Structure SET = new SetStructure();

  private static final Structure SORTED_SET = new SortedSetStructure();

  private static final Structure MAP = new MapStructure();

  private static final Structure STRING_MAP = new StringMapStructure();

  static final <E> Structure<Opt<E>,Nothing,E> opt(){
    return OPT;
  }

  static final <E> Structure<Optional<E>,Nothing,E> optional(){
    return OPTIONAL;
  }

  static final <E> Structure<IList<E>,Nothing,E> list(){
    return LIST;
  }

  static final <E> Structure<IList<E>,Nothing,E> set(){
    return SET;
  }

  static final <E> Structure<IList<E>,Nothing,E> sortedSet(){
    return SORTED_SET;
  }

  static final <K,V> Structure<IMap<K,V>,K,V> map(){
    return MAP;
  }

  static final <V> Structure<ISortedMap<String,V>,String,V> stringMap(){
    return STRING_MAP;
  }

  private final Class<C> clazz;

  private Structure(final Class<C> clazz) {
    this.clazz = clazz;
  }

  final Class<C> clazz() {
    return clazz;
  }

  abstract boolean isEmpty(C obj);

  abstract C empty();

  C convert(final JsonNode json, final CollectionType<C,K,E> collectionType) {
    return create(json, collectionType, collector());
  }

  Collector<E, ?, C> collector() {
    throw bug();
  }

  JsonNode toJson(final CollectionType<C,K,E> collectionType, final C collection) {
    final JsonFactory jf = collectionType.jf();
    return stream(collection).map(e->collectionType.elementType().toJson(e)).collect(jf.toJsonList());
  }

  Stream<E> stream(final C collection) {
    throw bug();
  }

  final C create(
    final JsonNode json, final CollectionType<C,K,E> collectionType, final Collector<E, ?, C> collector
  ) {
    return
      (
        ( (json instanceof JsonNull)
          ? XStream.<JsonNode>empty()
          : stream(json)
        )
        .map(n->collectionType.elementType().parse(n))
      )
      .collect(collector)
    ;
  }

  private final XStream<JsonNode> stream(final JsonNode n){
    if(n.jsonNodeType().equals(JsonNodeType.LIST)) return n.asList().stream();
    else if(n.jsonNodeType().equals(JsonNodeType.NULL)) return XStream.empty();
    else return XStream.of(n);
  }

  abstract C createCollection(final Collection<? extends E> collection);


  private static final class OptStructure<E> extends Structure<Opt<E>,Nothing,E> {

    private OptStructure() {
      super((Class)Opt.class);
    }

    @Override
    boolean isEmpty(final Opt<E> opt) {
      return !opt.isPresent();
    }

    @Override
    Opt<E> empty() {
      return Opt.empty();
    }

    @Override
    JsonNode toJson(final CollectionType<Opt<E>,Nothing,E> collectionType, final Opt<E> opt) {
      final JsonFactory jf = collectionType.jf();
      return opt.map(e->collectionType.elementType().toJson(e)).orElseGet(jf::jsonNull);
    }

    @Override
    Opt<E> convert(final JsonNode json, final CollectionType<Opt<E>,Nothing,E> collectionType) {
      return json.isNull()
        ? Opt.empty()
        : Opt.of(collectionType.elementType().parse(json))
      ;
    }

    @Override
    Opt<E> createCollection(final Collection<? extends E> collection) {
      return atMostOne(collection);
    }

  }


  private static final class OptionalStructure<E> extends Structure<Optional<E>,Nothing,E> {

    private OptionalStructure() {
      super((Class)Optional.class);
    }

    @Override
    boolean isEmpty(final Optional<E> opt) {
      return !opt.isPresent();
    }

    @Override
    Optional<E> empty() {
      return Optional.empty();
    }

    @Override
    JsonNode toJson(final CollectionType<Optional<E>,Nothing,E> collectionType, final Optional<E> opt) {
      final JsonFactory jf = collectionType.jf();
      return opt.map(e->collectionType.elementType().toJson(e)).orElseGet(jf::jsonNull);
    }

    @Override
    Optional<E> convert(final JsonNode json, final CollectionType<Optional<E>,Nothing,E> collectionType) {
      return json.isNull()
        ? Optional.empty()
        : Optional.of(collectionType.elementType().parse(json))
      ;
    }

    @Override
    Optional<E> createCollection(final Collection<? extends E> collection) {
      return toOptional(atMostOne(collection));
    }
  }


  private static final class ListStructure<E> extends Structure<IList<E>,Nothing,E> {

    private ListStructure() {
      super((Class)IList.class);
    }

    @Override
    boolean isEmpty(final IList<E> list) {
      return list.isEmpty();
    }

    @Override
    IList<E> empty() {
      return emptyList();
    }

    @Override
    Stream<E> stream(final IList<E> list) {
      return list.stream();
    }

    @Override
    Collector<E, ?, IList<E>> collector() {
      return toIList();
    }

    @Override
    IList<E> createCollection(final Collection<? extends E> collection) {
      return listFrom(collection);
    }
  }


  private static final class SetStructure<E> extends Structure<ISet<E>,Nothing,E> {

    private SetStructure() {
      super((Class)ISet.class);
    }

    @Override
    boolean isEmpty(final ISet<E> set) {
      return set.isEmpty();
    }

    @Override
    ISet<E> empty() {
      return emptySet();
    }

    @Override
    Stream<E> stream(final ISet<E> set) {
      return set.stream();
    }

    @Override
    Collector<E, ?, ISet<E>> collector() {
      return toISet();
    }

    @Override
    ISet<E> createCollection(final Collection<? extends E> collection) {
      return setFrom(collection);
    }
  }


  private static final class SortedSetStructure<E extends Comparable<? super E>>
  extends Structure<ISortedSet<E>,Nothing,E> {

    private SortedSetStructure() {
      super((Class)ISortedSet.class);
    }

    @Override
    boolean isEmpty(final ISortedSet<E> set) {
      return set.isEmpty();
    }

    @Override
    ISortedSet<E> empty() {
      return emptySortedSet();
    }

    @Override
    Stream<E> stream(final ISortedSet<E> set) {
      return set.stream();
    }

    @Override
    Collector<E, ?, ISortedSet<E>> collector() {
      return toISortedSet();
    }
    @Override
    ISortedSet<E> createCollection(final Collection<? extends E> collection) {
      return sortedSetFrom(collection);
    }
  }


  private static final class MapStructure<K,V> extends Structure<IMap<K,V>,K,V> {

    private MapStructure() {
      super((Class)IMap.class);
    }

    @Override
    boolean isEmpty(final IMap<K,V> map) {
      return map.isEmpty();
    }

    @Override
    IMap<K, V> empty() {
      return emptyMap();
    }

    @Override
    JsonNode toJson(final CollectionType<IMap<K,V>,K,V> mapType, final IMap<K,V> map) {
      final JsonFactory jf = mapType.jf();
      final TypeSupport<K> keyType = mapType.keyType().get();
      final TypeSupport<V> valueType = mapType.elementType();
      return
        map.entrySet().stream()
        .map(e->pair(
          keyType.toJson(e.getKey()),
          valueType.toJson(e.getValue())
        ))
        .sorted((p1,p2)->p1.getKey().compareTo(p2.getKey()))
        .map(p->
          Stream.of(pair("k", p.getKey()), pair("v", p.getValue()))
          .collect(jf.toJsonObject())
        )
        .collect(jf.toJsonList())
      ;
    }

    @Override
    final IMap<K,V> convert(final JsonNode json, final CollectionType<IMap<K,V>,K,V> mapType) {
      final TypeSupport<K> keyType = mapType.keyType().get();
      final TypeSupport<V> valueType = mapType.elementType();
      final Stream<JsonNode> stream = (json instanceof JsonNull)
        ? Stream.<JsonNode>empty()
        : json.asList().stream()
      ;
      return stream
        .map(JsonNode::asObject)
        .map(n->pair(
          keyType.parse(n.get("k")),
          valueType.parse(n.get("v"))
        ))
        .collect(toIMap())
      ;
    }

    @Override
    IMap<K, V> createCollection(final Collection<? extends V> collection) {
      throw new UnsupportedOperationException();
    }

  }

  private static final class StringMapStructure<K extends Comparable<? super K>, V>
  extends Structure<ISortedMap<K,V>,K,V> {

    private StringMapStructure() {
      super((Class)ISortedMap.class);
    }

    @Override
    boolean isEmpty(final ISortedMap<K,V> map) {
      return map.isEmpty();
    }

    @Override
    ISortedMap<K, V> empty() {
      return emptySortedMap();
    }

    @Override
    JsonNode toJson(final CollectionType<ISortedMap<K,V>,K,V> mapType, final ISortedMap<K,V> map) {
      final JsonFactory jf = mapType.jf();
      final TypeSupport<K> keyType = mapType.keyType().get();
      final TypeSupport<V> valueType = mapType.elementType();
      return
        map.entrySet().stream()
        .collect(jf.toJsonObject(
          e->keyType.toJson(e.getKey()).asString(),
          e->valueType.toJson(e.getValue())
        ))
      ;
    }

    @Override
    final ISortedMap<K,V> convert(final JsonNode json, final CollectionType<ISortedMap<K,V>,K,V> mapType) {
      final JsonFactory jf = mapType.jf();
      final TypeSupport<K> keyType = mapType.keyType().get();
      final TypeSupport<V> valueType = mapType.elementType();
      final Stream<Entry<String,JsonNode>> stream = (json instanceof JsonNull)
        ? Stream.<Entry<String,JsonNode>>empty()
        : json.asObject().entrySet().stream()
      ;
      return stream.collect(toISortedMap(
        e->keyType.parse(jf.primitive(e.getKey())),
        e->valueType.parse(e.getValue())
      ));
    }

    @Override
    ISortedMap<K, V> createCollection(final Collection<? extends V> collection) {
      throw new UnsupportedOperationException();
    }
  }

}

