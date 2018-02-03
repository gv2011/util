package com.github.gv2011.util.icol.guava;

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

import static com.github.gv2011.util.ex.Exceptions.format;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiConsumer;

import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.ISet;
import com.google.common.collect.ImmutableList;

class IMapWrapper<K,V,M extends Map<K,V>> implements IMap<K,V>{

  protected final M delegate;

  IMapWrapper(final M delegate) {
    this.delegate = delegate;
  }

  @Override
  public final int size() {
    return delegate.size();
  }

  @Override
  public final boolean isEmpty() {
    return delegate.isEmpty();
  }

  @Override
  public final boolean containsKey(final Object key) {
    return delegate.containsKey(key);
  }

  @Override
  public final boolean containsValue(final Object value) {
    return delegate.containsValue(value);
  }

  @Override
  public final V get(final Object key) {
    final V value = delegate.get(key);
    if(value==null)
      throw new NoSuchElementException(format("Map has no value for key {}.", key));
    return value;
  }

  @Override
  public ISet<K> keySet() {
    return new ISetWrapper<>(delegate.keySet());
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public final IList<V> values() {
    return new IListWrapper(ImmutableList.copyOf(delegate.values()));
  }

  @Override
  public ISet<Entry<K, V>> entrySet() {
    return new ISetWrapper<>(delegate.entrySet());
  }

  @Override
  public final boolean equals(final Object o) {
    return delegate.equals(o);
  }

  @Override
  public final int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public final V getOrDefault(final Object key, final V defaultValue) {
    return delegate.getOrDefault(key, defaultValue);
  }

  @Override
  public final void forEach(final BiConsumer<? super K, ? super V> action) {
    delegate.forEach(action);
  }

  @Override
  public final Optional<V> tryGet(final Object key) {
    return Optional.ofNullable(delegate.get(key));
  }

  @Override
  public final Entry<K, V> single() {
    return entrySet().single();
  }

}
