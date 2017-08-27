package com.github.gv2011.util.icol.guava;

import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.format;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.MapBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

abstract class AbstractIMapBuilder<M extends IMap<K,V>, K, V, B extends MapBuilder<M,K,V,B>>
implements MapBuilder<M,K,V,B>{

  protected final Map<K,V> map = Collections.synchronizedMap(new HashMap<>());

  protected abstract B self();

  @Override
  public B put(final K key, final V value) {
    final V actual = map.putIfAbsent(key, value);
    if(actual!=null) throw new IllegalArgumentException(format("Map has already an entry for key {}.", key));
    return self();
  }

  @Override
  public B tryPut(final K key, final V value) {
    map.putIfAbsent(key, value);
    return self();
  }

  @Override
  public B putAll(final Map<? extends K, ? extends V> map) {
    final ImmutableMap<K,V> copy = ImmutableMap.copyOf(map);
    synchronized(this.map){
      verify(copy.entrySet().stream()
        .allMatch(e->e.getKey()!=null && e.getValue()!=null && !map.containsKey(e.getKey()))
      );
      this.map.putAll(copy);
    }
    return self();
  }

  @Override
  public B putAll(final IMap<? extends K, ? extends V> map) {
    synchronized(this.map){
      verify(map.entrySet().stream().allMatch(e->!map.containsKey(e.getKey())));
      this.map.putAll(map);
    }
    return self();
  }

  @Override
  public B tryPutAll(final Map<? extends K, ? extends V> map) {
    final ImmutableMap<K,V> copy = ImmutableMap.copyOf(map);
    synchronized(this.map){
      copy.entrySet().stream().forEach(e->this.map.putIfAbsent(notNull(e.getKey()), notNull(e.getValue())));
    }
    return self();
  }

  @Override
  public B tryPutAll(final IMap<? extends K, ? extends V> map) {
    synchronized(this.map){
      map.entrySet().stream().forEach(e->this.map.putIfAbsent(e.getKey(), e.getValue()));
    }
    return self();
  }


  @Override
  public B putAll(final Collection<? extends Entry<? extends K, ? extends V>> map) {
    final ImmutableMap<K,V> copy = ImmutableMap.copyOf(map);
    synchronized(this.map){
      verify(copy.entrySet().stream()
        .allMatch(e->e.getKey()!=null && e.getValue()!=null && !this.map.containsKey(e.getKey()))
      );
      this.map.putAll(copy);
    }
    return self();
  }

  @Override
  public B tryPutAll(final Collection<? extends Entry<? extends K, ? extends V>> map) {
    final ImmutableMap<K,V> copy = ImmutableMap.copyOf(map);
    synchronized(this.map){
      copy.entrySet().stream().forEach(e->this.map.putIfAbsent(notNull(e.getKey()), notNull(e.getValue())));
    }
    return self();
  }

  @Override
  public ISortedMap<K, V> build(final Comparator<? super K> comparator) {
    synchronized(map){
      return new ISortedMapWrapper<>(ImmutableSortedMap.copyOf(map, comparator));
    }
  }


}