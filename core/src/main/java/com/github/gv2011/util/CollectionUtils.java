package com.github.gv2011.util;

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




import static com.github.gv2011.util.ServiceLoaderUtils.lazyServiceLoader;
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.notYetImplemented;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import com.github.gv2011.util.icol.ICollectionFactory;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;

public class CollectionUtils {

  private CollectionUtils(){staticClass();}

  private static final Constant<ICollectionFactory> ICOLF = lazyServiceLoader(ICollectionFactory.class);

  public static final ICollectionFactory iCollections(){return ICOLF.get();}

  public static final <E> IList.Builder<E> listBuilder(){return iCollections().listBuilder();};
  public static final <E> ISet.Builder<E>  setBuilder() {return iCollections().setBuilder();};
  public static final <E extends Comparable<? super E>> ISortedSet.Builder<E> sortedSetBuilder(){
    return iCollections().sortedSetBuilder();
    };
  public static final <K,V> IMap.Builder<K,V> mapBuilder(){return iCollections().mapBuilder();};
  public static final <K extends Comparable<? super K>,V> ISortedMap.Builder<K,V> sortedMapBuilder(){
    return iCollections().sortedMapBuilder();
    };

  public static final <T extends Comparable<? super T>> Collector<T, ?, NavigableSet<T>> toSortedSet(){
    return new SortedSetCollector<T,NavigableSet<T>>(){
      @Override
      public Function<NavigableSet<T>, NavigableSet<T>> finisher() {
        return Function.identity();
      }
    };
  }

  public static final <T extends Comparable<? super T>> Collector<T, ?, ISortedSet<T>> toISortedSet(){
    return iCollections().sortedSetCollector();
  }

  public static abstract class SortedSetCollector<T,R> implements Collector<T, NavigableSet<T>, R>{
    @Override
    public final Supplier<NavigableSet<T>> supplier() {
      return TreeSet::new;
    }
    @Override
    public final BiConsumer<NavigableSet<T>, T> accumulator() {
      return (s,e)->s.add(e);
    }
    @Override
    public final BinaryOperator<NavigableSet<T>> combiner() {
      return (s1,s2)->{s1.addAll(s2);return s1;};
    }
    @Override
    public Set<Characteristics> characteristics() {
      return EnumSet.of(Characteristics.UNORDERED);
    }
  }


  public static <T> Iterable<T> asIterable(final Supplier<Iterator<T>> iteratorSuppplier){
    return () -> iteratorSuppplier.get();
  }

  public static <T> T single(final Iterable<? extends T> collection){
    return single(collection, (n)->n==0?"No element.":"Multiple elements.");
  }

  public static <T> T single(final T[] array){
    final int size = array.length;
    verify(size!=0, "No element.");
    verify(size<2, size + " elements.");
    return notNull(array[0]);
  }

  public static <T> T single(final Iterator<? extends T> it){
    return single(it, i->i==0?"No element.":"Multiple elements.");
  }

  @SafeVarargs
  public static <T> T single(final Optional<? extends T>... optionals){
    return atMostOne(optionals).get();
  }

  @SafeVarargs
  public static <T> Optional<T> atMostOne(final Optional<? extends T>... optionals){
    return Arrays.stream(optionals).flatMap(CollectionUtils::stream).collect(toOptional());
  }

  public static <V> V get(final Map<?,? extends V> map, final Object key){
    final V result = map.get(key);
    if(result==null) {
      throw new NoSuchElementException(format("Map contains no element with key {}.", key));
    }
    return result;
  }

  public static <V> Optional<V> tryGet(final Map<?,? extends V> map, final Object key){
    return Optional.ofNullable(map.get(key));
  }

  public static <S,T> Function<S,XStream<T>> ifPresent(final Function<S,Optional<T>> optFunction){
    return s->stream(optFunction.apply(s));
  }

  public static <K,V> Pair<K,V> pair(final K key, final V value){
    return new Pair<>(key, value);
  }

  @Deprecated //use XStream method
  public static <T> XStream<T> stream(final Optional<? extends T> optional){
    return XStream.fromOptional(optional);
  }

  @Deprecated //use XStream method
  public static <T> XStream<T> stream(final T[] array){
    return XStream.of(array);
  }

  @Deprecated //use XStream method
  public static <T> XStream<T> stream(final Iterator<? extends T> iterator){
    return XStream.fromIterator(iterator);
  }

//  public static <T> XStream<T> stream(final Iterator<?> iterator, final Class<T> elementType){
//    return StreamSupport.stream(
//      Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
//      false
//    )
//    .map(elementType::cast)
//    ;
//  }


  @SafeVarargs
  public static <T> List<T> concat(final Collection<? extends T>... collections){
    Stream<T> s = Stream.empty();
    for(final Collection<? extends T> c: collections){
      s = Stream.concat(s, c.stream());
    }
    return Collections.unmodifiableList(s.collect(toList()));
  }


  public static <T> Function<T,Optional<T>> filter(final Predicate<T> predicate){
    return e->predicate.test(e) ? Optional.of(e) : Optional.empty();
  }


  public static <T> T single(final Iterable<? extends T> collection, final Function<Integer,String> message){
    return single(collection.iterator(), message);
  }

  public static <T> T single(final Iterator<? extends T> iterator, final Function<Integer,String> message){
    verify(iterator.hasNext(), ()->message.apply(0));
    final T result = notNull(iterator.next(), ()->message.apply(0));
    verify(!iterator.hasNext(), ()->message.apply(2));
    return result;
  }

  public static <T> Optional<T> atMostOne(final Iterable<? extends T> collection){
    return atMostOne(collection, ()->"Collection has more than one element.");
  }

  public static <T> Optional<T> atMostOne(
    final Iterable<? extends T> collection, final Supplier<String> moreThanOneMessage
  ){
    return atMostOne(collection.iterator(), moreThanOneMessage);
  }

  public static <T> Optional<T> atMostOne(
    final Iterator<? extends T> iterator, final Supplier<String> moreThanOneMessage
  ){
    if(!iterator.hasNext()) return Optional.empty();
    else{
      final Optional<T> result = Optional.of(iterator.next());
      verify(!iterator.hasNext(), moreThanOneMessage);
      return result;
    }
  }

  public static <T> IList<T> asList(final Optional<? extends T> optional){
    return notYetImplemented();
//    return optional
//      .map(e->{
//        final ArrayList<T> result = new ArrayList<>(1);
//        result.add(e);
//        return result;
//      })
//      .orElse(new ArrayList<T>(0))
//    ;
  }

  public static <T> Collector<T,?,ISet<T>>
  toISet(){
    return iCollections().setCollector();
  }

  public static <T> Collector<T,?,IList<T>>
  toIList(){
    return iCollections().listCollector();
  }

  public static <T, K, V> Collector<T, ?, IMap<K,V>>
  toIMap(
    final Function<? super T, ? extends K> keyMapper,
    final Function<? super T, ? extends V> valueMapper
  ) {
    return iCollections().mapCollector(keyMapper, valueMapper);
  }

  public static <E extends Entry<? extends K, ? extends V>, K, V> Collector<E, ?, IMap<K,V>>
  toIMap() {
    return iCollections().mapCollector();
  }

  public static <T, K extends Comparable<? super K>, V> Collector<T, ?, ISortedMap<K,V>>
  toISortedMap(
    final Function<? super T, ? extends K> keyMapper,
    final Function<? super T, ? extends V> valueMapper
  ) {
    return iCollections().sortedMapCollector(keyMapper, valueMapper);
  }

  public static <T extends Entry<? extends K, ? extends V>, K extends Comparable<? super K>, V>
  Collector<T, ?, ISortedMap<K,V>>
  toISortedMap() {
    return iCollections().sortedMapCollector(Entry::getKey, Entry::getValue);
  }

  public static <T, K, V> Collector<T, ?, SortedMap<K,V>>
  toISortedMap(
    final Function<? super T, ? extends K> keyMapper,
    final Function<? super T, ? extends V> valueMapper,
    final Comparator<K> comparator
  ) {
    return  new Collector<T,SortedMap<K,V>, SortedMap<K,V>>(){
      @Override
      public Supplier<SortedMap<K, V>> supplier() {
        return ()->new TreeMap<>(comparator);
      }
      @Override
      public BiConsumer<SortedMap<K, V>, T> accumulator() {
         return (b,t)->b.put(
           keyMapper.apply(notNull(t, ()->"Found null element in stream.")),
           valueMapper.apply(t)
         );
      }
      @Override
      public BinaryOperator<SortedMap<K, V>> combiner() {
        return (b1,b2)->{
          b1.putAll(b2);
          return b1;
        };
      }
      @Override
      public Function<SortedMap<K, V>, SortedMap<K, V>> finisher() {
        return Collections::unmodifiableSortedMap;
      }
      @Override
      public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.UNORDERED);
      }
    };
  }

  public static <T, K, V> Collector<T, ?, Map<K,V>>
  toMapOpt(
    final Function<? super T, ? extends K> keyMapper,
    final Function<? super T, Optional<? extends V>> valueMapper
  ) {
    return  new Collector<T,Map<K,V>, Map<K,V>>(){
      @Override
      public Supplier<Map<K, V>> supplier() {
        return HashMap::new;
      }
      @Override
      public BiConsumer<Map<K, V>, T> accumulator() {
         return (b,t)->{
           final Optional<? extends V> optValue = valueMapper.apply(
             notNull(t, ()->"Null element found in the stream.")
           );
           if(optValue.isPresent()){
             b.put(
               keyMapper.apply(t),
               optValue.get()
             );
           }
         };
      }
      @Override
      public BinaryOperator<Map<K, V>> combiner() {
        return (b1,b2)->{
          b1.putAll(b2);
          return b1;
        };
      }
      @Override
      public Function<Map<K, V>, Map<K, V>> finisher() {
        return Collections::unmodifiableMap;
      }
      @Override
      public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.UNORDERED);
      }
    };
  }

  public static <T> Collector<T,?,T> toSingle(){
    return toSingle(()->"Empty stream.");
  }

  public static <T> Collector<T,?,T> toSingle(final Supplier<String> msg){
    return new OptCollector<T,T>(){
      @Override
      public Function<AtomicReference<T>, T> finisher() {
        return r->notNull(r.get(), msg);
      }
    };
  }

  public static <T> Collector<T,?,Stream<T>> toSingleStream(){
    return new OptCollector<T,Stream<T>>(){
      @Override
      public Function<AtomicReference<T>, Stream<T>> finisher() {
        return r->Stream.of(notNull(r.get(), ()->"Empty stream."));
      }
    };
  }

  public static <T> Collector<T,?,Optional<T>> toOptional(){
    return new OptCollector<T,Optional<T>>(){
      @Override
      public Function<AtomicReference<T>, Optional<T>> finisher() {
        return r->Optional.ofNullable(r.get());
      }
    };
  }

  public static <T> Collector<T,?,Stream<T>> toOptionalStream(){
    return new OptCollector<T,Stream<T>>(){
      @Override
      public Function<AtomicReference<T>, Stream<T>> finisher() {
        return r->{final T v=r.get(); return v==null?Stream.empty():Stream.of(v);};
      }
    };
  }

  private static abstract class OptCollector<T,R> implements Collector<T,AtomicReference<T>,R>{
    @Override
    public Supplier<AtomicReference<T>> supplier() {
      return AtomicReference::new;
    }
    @Override
    public BiConsumer<AtomicReference<T>, T> accumulator() {
       return (r,e)->{
         final boolean success = r.compareAndSet(null, e);
         verify(
           success,
           ()->format(
             "Stream has more than one element. Previous element: {}, actual element: {}.",
             r.get(), e
           )
         );
       };
    }
    @Override
    public BinaryOperator<AtomicReference<T>> combiner() {
      return (r1,r2)->{
        final T v1 = r1.get();
        final T v2 = r2.get();
        verify(v1==null||v2==null);
        if(v2!=null) r1.set(v2);
        return r1;
      };
    }
    @Override
    public Set<Characteristics> characteristics() {
      return EnumSet.of(Characteristics.UNORDERED);
    }
  }

  @SafeVarargs
  public static <T> IList<T> listOf(final T... elements) {
    final IList.Builder<T> builder = iCollections().listBuilder();
    for(final T e: elements) builder.add(e);
    return builder.build();
  }

  @SafeVarargs
  public static <T> ISet<T> setOf(final T... elements) {
    final ISet.Builder<T> builder = iCollections().setBuilder();
    for(final T e: elements) builder.add(e);
    return builder.build();
  }


  @SafeVarargs
  public static <T extends Comparable<? super T>> ISortedSet<T> sortedSetOf(final T... elements) {
    final ISortedSet.Builder<T> builder = iCollections().sortedSetBuilder();
    for(final T e: elements) builder.add(e);
    return builder.build();
  }

  public static <K extends Comparable<? super K>,V> ISortedMap<K,V> sortedMapOf() {
    return iCollections().emptySortedMap();
  }

  public static <K extends Comparable<? super K>,V> ISortedMap<K,V> sortedMapOf(final K key,final V value) {
    return iCollections().<K,V>sortedMapBuilder().put(key, value).build();
  }

  public static <S,T> Iterable<T> mapIterable(
    final Iterable<? extends S> delegate, final Function<? super S, ? extends T> mapping
  ){
    return () -> mapIterator(delegate.iterator(), mapping);
  }

  public static <S,T> Iterator<T> mapIterator(
    final Iterator<? extends S> delegate, final Function<? super S, ? extends T> mapping
  ){
    return new Iterator<T>(){
      @Override
      public boolean hasNext() {return delegate.hasNext();}
      @Override
      public T next() {return mapping.apply(delegate.next());}
    };
  }

  public static final <A,B> Either<A,B> newThis(final A a){
    return EitherImp.newThis(a);
  }

  public static final <A,B> Either<A,B> newThat(final B b){
    return EitherImp.newThat(b);
  }

  public static final boolean optIs(final Optional<?> optional, final Object obj) {
    return optional.map(v->v.equals(obj)).orElse(false);
  }

  public static final <K,V> IMap<V,K> revert(final Map<? extends K,? extends V> map){
    return map.entrySet().stream().collect(toIMap(Entry::getValue,Entry::getKey));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static final <U,E extends U> Optional<U> upcast(final Optional<E> optional){
    return (Optional)optional;
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


}
