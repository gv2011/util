package com.github.gv2011.util.icol;

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
