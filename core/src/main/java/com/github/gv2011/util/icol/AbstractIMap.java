package com.github.gv2011.util.icol;

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




import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.CollectionUtils.toIList;
import static com.github.gv2011.util.CollectionUtils.toISet;
import static java.util.stream.Collectors.joining;

import java.util.Map;

import com.github.gv2011.util.Equal;

public abstract class AbstractIMap<K, V> implements IMap<K,V>{

  @Override
  public abstract ISet<K> keySet();

  @Override
  public abstract Opt<V> tryGet(final Object key);

  @Override
  public ISet<Entry<K, V>> entrySet() {
    return keySet().stream()
      .map(k->(Entry<K, V>)pair(k, get(k)))
      .collect(toISet())
    ;
  }

  @Override
  public Entry<K, V> single() {
    return entrySet().single();
  }

  @Override
  public int size() {
    return keySet().size();
  }

  @Override
  public boolean isEmpty() {
    return keySet().isEmpty();
  }

  @Override
  public boolean containsKey(final Object key) {
    return keySet().contains(key);
  }

  @Override
  public boolean containsValue(final Object value) {
    return values().contains(value);
  }

  @Override
  public IList<V> values() {
    return keySet().stream().map(this::get).collect(toIList());
  }


  @Override
  public int hashCode() {
    return entrySet().parallelStream().mapToInt(Entry::hashCode).sum();
  }

  @Override
  public boolean equals(final Object obj) {
    return Equal.equal(this, obj, Map.class, m->{
      if(size()!=m.size()) return false;
      else if(!keySet().equals(m.keySet())) return false;
      else return keySet().stream().allMatch(k->get(k).equals(m.get(k)));
    });
  }

  @Override
  public String toString() {
    return keySet().stream().map(k->k+"="+get(k)).collect(joining(", ","{","}"));
  }


}
