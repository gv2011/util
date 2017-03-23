package com.github.gv2011.util.icol;

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
    return tryGet(key).get();
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
