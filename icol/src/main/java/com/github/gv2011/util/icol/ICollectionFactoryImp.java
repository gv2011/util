package com.github.gv2011.util.icol;

import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.util.Comparator;

import com.github.gv2011.util.icol.IList.Builder;

final class ICollectionFactoryImp implements ICollectionFactory{

  @Override
  public <T> IList<T> emptyList() {
    return EmptyIList.empty();
  }

  @Override
  public <T> ISortedSet<T> emptySet() {
    return EmptyISet.empty();
  }

  @Override
  public <K, V> ISortedMap<K, V> emptyMap() {
    return EmptyIMap.instance();
  }

  @Override
  public <T> IList<T> listOf(final T element) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public <T> ISortedSet<T> setOf(final T element) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public <K, V> ISortedMap<K, V> mapOf(final K key, final V value) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public <E> Builder<E> listBuilder() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public <E> com.github.gv2011.util.icol.ISet.Builder<E> setBuilder() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public <E extends Comparable<? super E>> com.github.gv2011.util.icol.ISortedSet.Builder<E> sortedSetBuilder() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public <E> com.github.gv2011.util.icol.ISortedSet.Builder<E> sortedSetBuilder(final Comparator<? super E> comparator) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public <K, V> com.github.gv2011.util.icol.IMap.Builder<K, V> mapBuilder() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public <K extends Comparable<? super K>, V> com.github.gv2011.util.icol.ISortedMap.Builder<K, V> sortedMapBuilder() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public <K, V> com.github.gv2011.util.icol.ISortedMap.Builder<K, V>
      sortedMapBuilder(final Comparator<? super K> comparator) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

}
