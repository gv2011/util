package com.github.gv2011.util.icol;

/*-
 * %---license-start---
 * The MIT License (MIT)
 * %
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %
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
 * %---license-end---
 */



import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import com.github.gv2011.util.Builder;

public interface MapBuilder<M extends IMap<K,V>, K, V, B extends MapBuilder<M,K,V,B>> extends Builder<M>{

  B put(K key, V value);

  B tryPut(K key, V value);

  B putAll(Map<? extends K, ? extends V> map);

  B putAll(IMap<? extends K, ? extends V> map);

  B tryPutAll(Map<? extends K, ? extends V> map);

  B tryPutAll(IMap<? extends K, ? extends V> map);

  B putAll(Collection<? extends Entry<? extends K, ? extends V>> map);

  B tryPutAll(Collection<? extends Entry<? extends K, ? extends V>> map);

  ISortedMap<K,V> build(Comparator<? super K> keyComparator);
}
