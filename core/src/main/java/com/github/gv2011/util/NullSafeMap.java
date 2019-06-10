package com.github.gv2011.util;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2019 Vinz (https://github.com/gv2011)
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

import com.github.gv2011.util.icol.Opt;

public interface NullSafeMap<K,V> extends Map<K,V>{

  Opt<V> tryGet(Object key);

  Opt<V> tryRemove(Object key);

  /**
   * Returns the value to which the specified key is mapped,
   * or throws {@link NoSuchElementException} if this map contains no mapping for the key.
   *
   * This is a major difference to the behaviour of {@Map}, which is needed to avoid the
   * usage of {@code null}.
   *
   * {@link #tryGet} can be used if the presence of a mapping is not known.
   *
   * <p>More formally, if this map contains a mapping from a key
   * {@code k} to a value {@code v} such that key.equals(k))}, then this method returns
   * {@code v}; otherwise it throws {@link NoSuchElementException}.  (There can be at most one such mapping.)
   *
   * <p>This map must not contain {@code null} values.
   *
   * @param key the key whose associated value is to be returned. The behaviour is unspecified if key is {@code null}.
   * @return the value to which the specified key is mapped
   * @throws NoSuchElementException if the key is not mapped to any value
   *
   * @see java.util.Map#get(java.lang.Object)
   */
  @Override
  default V get(final Object key) {
    return tryGet(key).orElseThrow(()->new NoSuchElementException(format("No entry for key {}.", key)));
  }

  @Override
  default V remove(final Object key) {
    return tryRemove(key).orElseThrow(()->new NoSuchElementException(format("No entry for key {}.", key)));
  }


}
