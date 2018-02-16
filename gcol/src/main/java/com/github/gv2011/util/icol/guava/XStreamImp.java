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

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gv2011.util.XStream;

final class XStreamImp<T> implements XStream<T> {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(XStreamImp.class);

    static <E> XStreamImp<E> xStream(final Stream<E> s){
      if(s instanceof XStreamImp) return (XStreamImp<E>)s;
      else return wrap(s);
    }

    private static <E> XStreamImp<E> wrap(final Stream<E> s){
      return new XStreamImp<>(s);
    }

    private final Stream<T> delegate;

    private XStreamImp(final Stream<T> delegate) {
      this.delegate = delegate;
    }

    @Override
    public Iterator<T> iterator() {
      return delegate.iterator();
    }

    @Override
    public Spliterator<T> spliterator() {
      return delegate.spliterator();
    }

    @Override
    public boolean isParallel() {
      return delegate.isParallel();
    }

    @Override
    public void close() {
      delegate.close();
    }

    @Override
    public XStream<T> sequential() {
      return wrap(delegate.sequential());
    }

    @Override
    public XStream<T> parallel() {
      return wrap(delegate.parallel());
    }

    @Override
    public XStream<T> unordered() {
      return wrap(delegate.unordered());
    }

    @Override
    public XStream<T> onClose(final Runnable closeHandler) {
      return wrap(delegate.onClose(closeHandler));
    }

    @Override
    public XStream<T> filter(final Predicate<? super T> predicate) {
      return wrap(delegate.filter(predicate));
    }

    @Override
    public <R> XStream<R> map(final Function<? super T, ? extends R> mapper) {
      return wrap(delegate.map(mapper));
    }

    @Override
    public IntStream mapToInt(final ToIntFunction<? super T> mapper) {
      return delegate.mapToInt(mapper);
    }

    @Override
    public LongStream mapToLong(final ToLongFunction<? super T> mapper) {
      return delegate.mapToLong(mapper);
    }

    @Override
    public DoubleStream mapToDouble(final ToDoubleFunction<? super T> mapper) {
      return delegate.mapToDouble(mapper);
    }

    @Override
    public <R> XStream<R> flatMap(final Function<? super T, ? extends Stream<? extends R>> mapper) {
      return wrap(delegate.flatMap(mapper));
    }

    @Override
    public IntStream flatMapToInt(final Function<? super T, ? extends IntStream> mapper) {
      return delegate.flatMapToInt(mapper);
    }

    @Override
    public LongStream flatMapToLong(final Function<? super T, ? extends LongStream> mapper) {
      return delegate.flatMapToLong(mapper);
    }

    @Override
    public DoubleStream flatMapToDouble(final Function<? super T, ? extends DoubleStream> mapper) {
      return delegate.flatMapToDouble(mapper);
    }

    @Override
    public XStream<T> distinct() {
      return wrap(delegate.distinct());
    }

    @Override
    public XStream<T> sorted() {
      return wrap(delegate.sorted());
    }

    @Override
    public XStream<T> sorted(final Comparator<? super T> comparator) {
      return wrap(delegate.sorted(comparator));
    }

    @Override
    public XStream<T> peek(final Consumer<? super T> action) {
      return wrap(delegate.peek(action));
    }

    @Override
    public XStream<T> limit(final long maxSize) {
      return wrap(delegate.limit(maxSize));
    }

    @Override
    public XStream<T> skip(final long n) {
      return wrap(delegate.skip(n));
    }

    @Override
    public void forEach(final Consumer<? super T> action) {
      delegate.forEach(action);
    }

    @Override
    public void forEachOrdered(final Consumer<? super T> action) {
      delegate.forEachOrdered(action);
    }

    @Override
    public Object[] toArray() {
      return delegate.toArray();
    }

    @Override
    public <A> A[] toArray(final IntFunction<A[]> generator) {
      return delegate.toArray(generator);
    }

    @Override
    public T reduce(final T identity, final BinaryOperator<T> accumulator) {
      return delegate.reduce(identity, accumulator);
    }

    @Override
    public Optional<T> reduce(final BinaryOperator<T> accumulator) {
      return delegate.reduce(accumulator);
    }

    @Override
    public <U> U reduce(
      final U identity, final BiFunction<U, ? super T, U> accumulator, final BinaryOperator<U> combiner
    ) {
      return delegate.reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(
      final Supplier<R> supplier, final BiConsumer<R, ? super T> accumulator, final BiConsumer<R, R> combiner
    ) {
      return delegate.collect(supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(final Collector<? super T, A, R> collector) {
      return delegate.collect(collector);
    }

    @Override
    public Optional<T> min(final Comparator<? super T> comparator) {
      return delegate.min(comparator);
    }

    @Override
    public Optional<T> max(final Comparator<? super T> comparator) {
      return delegate.max(comparator);
    }

    @Override
    public long count() {
      return delegate.count();
    }

    @Override
    public boolean anyMatch(final Predicate<? super T> predicate) {
      return delegate.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(final Predicate<? super T> predicate) {
      return delegate.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(final Predicate<? super T> predicate) {
      return delegate.noneMatch(predicate);
    }

    @Override
    public Optional<T> findFirst() {
      return delegate.findFirst();
    }

    @Override
    public Optional<T> findAny() {
      return delegate.findAny();
    }

    @Override
    public <E> XStream<E> filter(final Class<E> clazz) {
      return wrap(delegate.filter(e->clazz.isInstance(e)).map(e->clazz.cast(e)));
    }

    @Override
    public <R> XStream<R> flatOptional(final Function<? super T, ? extends Optional<? extends R>> mapper) {
      return wrap(
        delegate.map(mapper).filter(Optional::isPresent).map(Optional::get)
      );
    }

    @Override
    public XStream<T> concat(final Stream<? extends T> other) {
      return wrap(Stream.concat(delegate, other));
    }

  }



