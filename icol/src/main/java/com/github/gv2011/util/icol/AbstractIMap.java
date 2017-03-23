package com.github.gv2011.util.icol;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;

abstract class AbstractIMap<M extends Map<K,V>, K,V> extends AbstractMap<K,V> implements IMap<K,V>{

  protected abstract M delegate();

  @Override
  public final V get(final Object key) {
    return tryGet(key).get();
  }

  @Override
  public final Optional<V> tryGet(final Object key) {
    return Optional.ofNullable(delegate().get(key));
  }

  @Override
  public final V getOrDefault(final Object key, final V defaultValue) {
    return delegate().getOrDefault(key, defaultValue);
  }

  @Override
  public final Entry<K, V> single() {
    if(size()!=1) throw new IllegalStateException();
    return delegate().entrySet().iterator().next();
  }

  @Override
  public final Entry<K, V> first() {
    return delegate().entrySet().iterator().next();
  }

  @Override
  public final Optional<Entry<K, V>> tryGetFirst() {
    return Optional.of(first());
  }

  @Override
  public final int size() {
    return delegate().size();
  }

  @Override
  public final boolean isEmpty() {
    return false;
  }

  @Override
  public abstract ISet<K> keySet();

  @Override
  public final IList<V> values() {
    return new IListImp<>(delegate().values());
  }

//  @Override
//  public final ISet<Entry<K, V>> entrySet() {
//    return new ISetImp<>(delegate().entrySet());
//  }
//
}
