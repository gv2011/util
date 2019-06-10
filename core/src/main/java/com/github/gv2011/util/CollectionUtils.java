package com.github.gv2011.util;

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
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.gv2011.util.icol.ICollection;
import com.github.gv2011.util.icol.Opt;

public class CollectionUtils {

  private CollectionUtils(){staticClass();}

  public static final <T extends Comparable<? super T>> Collector<T, ?, NavigableSet<T>> toSortedSet(){
    return new SortedSetCollector<T,NavigableSet<T>>(){
      @Override
      public Function<NavigableSet<T>, NavigableSet<T>> finisher() {
        return Function.identity();
      }
    };
  }

  public static final <K extends Comparable<? super K>,V,E> Collector<E,?,NavigableMap<K,V>> toSortedMap(
    final Function<E,K> keyMapper, final Function<E,V> valueMapper
  ) {
    return Collectors.toMap(
      keyMapper,
      valueMapper,
      (v1,v2) ->{ throw new RuntimeException(String.format("Duplicate key for values %s and %s", v1, v2));},
      TreeMap::new
    );
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

  /**
   * Use {@link ICollection#single}.
   */
  @Deprecated
  public static <T> T single(final ICollection<? extends T> collection){
    return collection.single();
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

  public static <V> V get(final Map<?,? extends V> map, final Object key){
    final V result = map.get(key);
    if(result==null) {
      throw new NoSuchElementException(format("Map contains no element with key {}.", key));
    }
    return result;
  }

  public static <V> Opt<V> tryGet(final Map<?,? extends V> map, final Object key){
    return Opt.ofNullable(map.get(key));
  }

  public static <K,V> Pair<K,V> pair(final K key, final V value){
    return new Pair<>(key, value);
  }

  public static <T> XStream<T> stream(final Optional<? extends T> optional){
    return XStream.fromOptional(optional);
  }

  public static <T> Opt<T> filter(final Opt<T> optional, final Predicate<? super T> predicate){
    if(optional.isPresent()) {
      if(predicate.test(optional.get())) return optional;
      else return Opt.empty();
    }
    else return optional;
  }

  public static <T> XStream<T> stream(final T[] array){
    return XStream.of(array);
  }

  public static <T> XStream<T> stream(final Iterator<? extends T> iterator){
    return XStream.fromIterator(iterator);
  }


  @SafeVarargs
  public static <T> List<T> concat(final Collection<? extends T>... collections){
    Stream<T> s = Stream.empty();
    for(final Collection<? extends T> c: collections){
      s = Stream.concat(s, c.stream());
    }
    return Collections.unmodifiableList(s.collect(toList()));
  }


  public static <T> Function<T,Opt<T>> filter(final Predicate<T> predicate){
    return e->predicate.test(e) ? Opt.of(e) : Opt.empty();
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

  public static <T> Opt<T> atMostOne(final Iterable<? extends T> collection){
    return atMostOne(collection, ()->"Collection has more than one element.");
  }

  public static <T> Optional<T> toOptional(final Opt<? extends T> opt){
    return opt.isPresent() ? Optional.of(opt.get()) : Optional.empty();
  }

  public static <T> Opt<T> atMostOne(
    final Iterable<? extends T> collection, final Supplier<String> moreThanOneMessage
  ){
    return atMostOne(collection.iterator(), moreThanOneMessage);
  }


  public static <T> Opt<T> atMostOne(
    final Iterator<? extends T> iterator, final Supplier<String> moreThanOneMessage
  ){
    if(!iterator.hasNext()) return Opt.empty();
    else{
      final Opt<T> result = Opt.of(iterator.next());
      verify(!iterator.hasNext(), moreThanOneMessage);
      return result;
    }
  }


  public static <T, K, V> Collector<T, ?, Map<K,V>>
  toMapOpt(
    final Function<? super T, ? extends K> keyMapper,
    final Function<? super T, Optional<? extends V>> valueMapper
  ) {
    return new Collector<T,Map<K,V>, Map<K,V>>(){
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

  @Deprecated
  public static <T> Collector<T,?,Optional<T>> toOptional(){
    return new OptCollector<T,Optional<T>>(){
      @Override
      public Function<AtomicReference<T>, Optional<T>> finisher() {
        return r->Optional.ofNullable(r.get());
      }
    };
  }

  public static <T> Collector<T,?,Opt<T>> toOpt(){
    return new OptCollector<T,Opt<T>>(){
      @Override
      public Function<AtomicReference<T>, Opt<T>> finisher() {
        return r->Opt.ofNullable(r.get());
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

  @Deprecated
  public static final boolean optIs(final Optional<?> optional, final Object obj) {
    return optional.map(v->v.equals(obj)).orElse(false);
  }


  public static final <N> XStream<N> recursiveStream(
    final N root, final Function<N,? extends Stream<? extends N>> children
  ){
    return XStream.of(root).concat(children.apply(root).flatMap(c->recursiveStream(c,children)));
  }

    /**
     * Returns a sequential ordered {@code IntStream} from {@code fromExclusive}
     * (exclusive) to {@code toInclusive} (inclusive) by an incremental or decremental step of
     * {@code 1}.
     *
     * <p>An equivalent sequence of increasing values can be produced
     * sequentially using a {@code for} loop as follows:
     * <pre>{@code
     *     final int increment = endExclusive >= startInclusive ? 1 : -1;
     *     for (int i = startInclusive; i != endExclusive ; i+=increment) { ... }
     * }</pre>
     *
     * @param startExclusive the (exclusive) initial value
     * @param endInclusive the inclusive lower bound
     * @return a sequential {@code IntStream} for the range of {@code int}
     *         elements from higher to lower values
     */
  public static IntStream intRange(final int startInclusive, final int endExclusive){
    final int size = endExclusive - startInclusive;
    if(size>=1) return IntStream.range(startInclusive, endExclusive);
    else return IntStream.range(0, size).map(i->startInclusive-i);
  }

  public static int listHashCode(final Iterable<?> list){
    int hashCode = 1;
    for(final Object e: list) hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
    return hashCode;
  }


  public static boolean listEquals(final List<?> list1, final Object o){
    final boolean result;
    if (list1 == o) result = true;
    else if (!(o instanceof List)) result = false;
    else{
      final ListIterator<?> e1 = list1.listIterator();
      final ListIterator<?> e2 = ((List<?>) o).listIterator();
      boolean differenceFound = false;
      while (e1.hasNext() && e2.hasNext() && !differenceFound) {
        final Object o1 = e1.next();
        final Object o2 = e2.next();
        if (!(o1==null ? o2==null : o1.equals(o2))) differenceFound = true;
      }
      result = !differenceFound && !e1.hasNext() && !e2.hasNext();
    }
    return result;
  }

  public static <E> Opt<E> tryGetFirst(final SortedSet<E> sortedSet) {
    return sortedSet.isEmpty() ? Opt.empty() : Opt.of(sortedSet.first());
  }

  public static <K> Opt<K> tryGetFirstKey(final SortedMap<K,?> sortedMap) {
    return sortedMap.isEmpty() ? Opt.empty() : Opt.of(sortedMap.firstKey());
  }

}
