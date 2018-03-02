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
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verifyEqual;
import static java.util.stream.Collectors.toMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.ann.Nullable;

final class SoftIndexImp<K,V> implements SoftIndex<K,V>{

  private static final Logger LOG = getLogger(SoftIndexImp.class);

  private final Function<K, Optional<? extends V>> function;
  private final Object lock = new Object();
  private SoftReference<Set<WeakReference<Pair<K,Optional<V>>>>> data = new SoftReference<>(null);
  private SoftReference<Map<K,Optional<V>>> index = new SoftReference<>(null);

  private final Consumer<Pair<K,Optional<V>>> addedListener;

  SoftIndexImp(final Function<K, Optional<? extends V>> function, final Consumer<Pair<K,Optional<V>>> addedListener) {
    this.function = function;
    this.addedListener = addedListener;
  }

  private Set<WeakReference<Pair<K,Optional<V>>>> data(){
    @Nullable Set<WeakReference<Pair<K,Optional<V>>>> result = data.get();
    if(result==null) {
      synchronized(lock) {
        result = data.get();
        if(result==null) {
          result = new HashSet<>();
          data = new SoftReference<>(result);
        }
      }
    }
    return notNull(result);
  }

  private Map<K,Optional<V>> index(){
    @Nullable Map<K, Optional<V>> result = index.get();
    if(result==null) {
      synchronized(lock) {
        result = index.get();
        if(result==null) {
          @Nullable
          final Set<WeakReference<Pair<K, Optional<V>>>> set = data();
          result =
            set.stream()
            .map(WeakReference::get)
            .filter(p->p!=null)
            .collect(toMap(Pair::getKey, Pair::getValue))
          ;
          index = new SoftReference<>(result);
        }
      }
    }
    return notNull(result);
  }

  @Override
  public Optional<V> tryGet(final K key) {
    final Optional<V> result;
    final Map<K, Optional<V>> index = index();
    @Nullable final Optional<V> current = index.get(key);
    if(current==null) {
      result = tryAddSync(key, index);
    }
    else result = notNull(current);
    return result;
  }

  private Optional<V> tryAddSync(final K key, final Map<K, Optional<V>> index) {
    synchronized(lock) {
      final Set<WeakReference<Pair<K, Optional<V>>>> set = data();
      final Optional<Pair<K, Optional<V>>> existing = set.parallelStream()
      .map(WeakReference::get)
      .filter(p->p!=null)
      .filter(p->p.getKey().equals(key))
      .findAny();
      Optional<V> result;
      final @Nullable Optional<V> created;
      if(!existing.isPresent()) {
        created = function.apply(key).map(v->v);
        LOG.debug("Adding entry for {}.", key);
        set.add(new WeakReference<>(pair(key, created)));
        result = created;
      }
      else {
        LOG.debug("There is already an entry for {}.", key);
        created = null;
        result = existing.get().getValue();
        verifyEqual(created, result);
      }
      index.put(key, result);
      if(created!=null) addedListener.accept(pair(key, created));
      return result;
    }
  }


  @Override
  public V get(final K key) {
    return tryGet(key).get();
  }

}