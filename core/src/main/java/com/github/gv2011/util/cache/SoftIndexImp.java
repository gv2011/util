package com.github.gv2011.util.cache;

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
import static com.github.gv2011.util.CollectionUtils.pair;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;

import com.github.gv2011.util.FConsumer;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.icol.Opt;

final class SoftIndexImp<K,V> implements SoftIndex<K,V>{

  @SuppressWarnings("unused")
private static final Logger LOG = getLogger(SoftIndexImp.class);

  private final Object lock = new Object();

  private final Function<K, Opt<? extends V>> function;
  private final Map<K,Opt<V>> index = new HashMap<>();

  private final FConsumer<Pair<K,Opt<V>>> addedListener;

  SoftIndexImp(
    final Function<K, Opt<? extends V>> function,
    final FConsumer<Pair<K,Opt<V>>> addedListener
  ) {
    this.function = function;
    this.addedListener = addedListener;
  }


  @Override
  public Opt<V> tryGet(final K key) {
    synchronized(lock) {
      Opt<V> result;
      final @Nullable Opt<V> opt = index.get(key);
      if(opt!=null) return result = opt;
      else {
        result = function.apply(key).map(v->v);
        index.put(key, result);
        addedListener.apply(pair(key, result));
      }
      return result;
    }
  }

  @Override
  public Opt<Opt<V>> getIfPresent(final K key) {
    synchronized(lock) {
      return Opt.ofNullable(index.get(key));
    }
  }

  @Override
  public V get(final K key) {
    return tryGet(key).get();
  }

}
