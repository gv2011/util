package com.github.gv2011.util.icol;

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
import static com.github.gv2011.util.ex.Exceptions.format;

import java.util.Collection;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Nothing;
import com.github.gv2011.util.ann.Nullable;

public interface Opt<E> extends ISet<E>, Constant<E>{

  public static <E> Opt<E> of(final E element){
    return ICollections.single(element);
  }

  public static <E> Opt<E> ofOptional(final Optional<? extends E> optional){
    return optional.isPresent() ? of(optional.get()) : empty();
  }

  public static <E> Opt<E> ofNullable(@Nullable final E element){
    return element==null ? empty() : of(element);
  }

  @SuppressWarnings("unchecked")
  public static <E> Opt<E> empty(){
    return ICollections.EMPTY;
  }

  @Override
  E get();

  boolean isPresent();

  Opt<E> filter(final Predicate<? super E> predicate);

  <U> Opt<U> map(final Function<? super E, ? extends U> mapper);

  default Opt<Nothing> ifPresent(final Consumer<? super E> consumer){
    return map(e->{consumer.accept(e);return Nothing.INSTANCE;});
  }

  <U> Opt<U> flatMap(final Function<? super E, ? extends Opt<? extends U>> mapper);

  Opt<E> or(final Supplier<? extends Opt<? extends E>> supplier);

  E orElse(final E other);

  E orElseGet(final Supplier<? extends E> supplier);

  <X extends Throwable> E orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X;

  @Override
  default boolean add(final E e) {
    throw new UnsupportedOperationException();
  }

  @Override
  default boolean remove(final Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  default boolean addAll(final Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  default boolean isEmpty() {
    return !isPresent();
  }

  @Override
  default Spliterator<E> spliterator() {
    if(isPresent()) return Spliterators.spliterator(new Object[]{get()}, 0);
    else return Spliterators.emptySpliterator();
  }

  @Override
  default boolean removeAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  default void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  default boolean retainAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unchecked")
  default Opt<E> merge(final Opt<? extends E> other){
    if(!isPresent()) return (Opt<E>) other;
    else if(!other.isPresent()) return this;
    else throw new IllegalStateException(
      format("Both optional values are present: ({} and {}).", this.get(), other.get())
    );
  }

  @Override
  default Object[] toArray() {
    Object[] result;
    if(isPresent()) {
      result = new Object[1];
      result[0] = get();
    }
    else result = new Object[0];
    return result;
  }

  @Override
  public Opt<E> subtract(final Collection<?> other);

}
