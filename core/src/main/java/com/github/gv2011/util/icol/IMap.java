package com.github.gv2011.util.icol;

import static com.github.gv2011.util.ex.Exceptions.format;

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




import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface IMap<K,V> extends Map<K,V>{

  public static interface Builder<K,V> extends MapBuilder<IMap<K,V>,K,V,Builder<K,V>>{}

  @Override
  ISet<K> keySet();

  @Override
  ICollection<V> values();

  @Override
  ISet<Entry<K, V>> entrySet();

  @Override
  default V get(final Object key) {
    return tryGet(key).orElseThrow(()->new NoSuchElementException(format("No entry for key {}.", key)));
  }

  Optional<V> tryGet(Object key);

  @Override
  default V getOrDefault(final Object key, final V defaultValue) {
    return tryGet(key).orElse(defaultValue);
  }

  Entry<K, V> single();

  default Entry<K, V> first(){
    if(isEmpty()) throw new NoSuchElementException();
    else return entrySet().iterator().next();
  }

  default Optional<Entry<K, V>> tryGetFirst(){
    if(isEmpty()) return Optional.empty();
    else return Optional.of(entrySet().iterator().next());
  }

  @Override
  @Deprecated
  default V put(final K key, final V value){
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default V remove(final Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default void putAll(final Map<? extends K, ? extends V> m) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default V putIfAbsent(final K key, final V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default boolean remove(final Object key, final Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default boolean replace(final K key, final V oldValue, final V newValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default V replace(final K key, final V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  default V merge(final K key, final V value, final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }


}
