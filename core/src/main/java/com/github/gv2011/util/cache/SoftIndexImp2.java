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
import static java.util.stream.Collectors.toMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;

import com.github.gv2011.util.FConsumer;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.icol.Opt;

final class SoftIndexImp2<K,V> implements SoftIndex<K,V>{

  private static final Logger LOG = getLogger(SoftIndexImp2.class);

  private final Function<K, Opt<? extends V>> function;
  private final Object lock = new Object();
  private SoftReference<Set<WeakReference<Pair<K,Opt<V>>>>> data = new SoftReference<>(null);
  private SoftReference<Map<K,Opt<V>>> index = new SoftReference<>(null);

  private final FConsumer<Pair<K,Opt<V>>> addedListener;

  SoftIndexImp2(final Function<K, Opt<? extends V>> function, final FConsumer<Pair<K,Opt<V>>> addedListener) {
    this.function = function;
    this.addedListener = addedListener;
  }

  private Set<WeakReference<Pair<K,Opt<V>>>> data(){
    @Nullable Set<WeakReference<Pair<K,Opt<V>>>> result = data.get();
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

  private Map<K,Opt<V>> index(){
    @Nullable Map<K, Opt<V>> result = index.get();
    if(result==null) {
      synchronized(lock) {
        result = index.get();
        if(result==null) {
          @Nullable
          final Set<WeakReference<Pair<K, Opt<V>>>> set = data();
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
  public Opt<V> tryGet(final K key) {
    final Opt<V> result;
    final Map<K, Opt<V>> index = index();
    @Nullable final Opt<V> current = index.get(key);
    if(current==null) {
      result = tryAddSync(key, index);
    }
    else result = notNull(current);
    return result;
  }

  @Override
  public Opt<Opt<V>> getIfPresent(final K key) {
    return Opt.ofNullable(index().get(key));
  }

  private Opt<V> tryAddSync(final K key, final Map<K, Opt<V>> index) {
    synchronized(lock) {
      final Set<WeakReference<Pair<K, Opt<V>>>> set = data();
      final Opt<Pair<K, Opt<V>>> existing = Opt.ofOptional(set.parallelStream()
        .map(WeakReference::get)
        .filter(p->p!=null)
        .filter(p->p.getKey().equals(key))
        .findAny()
      );
      Opt<V> result;
      if(!existing.isPresent()) {
        result = function.apply(key).map(v->v);
        LOG.debug("Adding entry for {}.", key);
        set.add(new WeakReference<>(pair(key, result)));
        index.put(key, result);
        addedListener.apply(pair(key, result));
      }
      else {
        LOG.debug("There is already an entry for {}.", key);
        result = existing.get().getValue();
      }
      return result;
    }
  }


  @Override
  public V get(final K key) {
    return tryGet(key).get();
  }

}
