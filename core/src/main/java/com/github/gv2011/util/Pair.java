package com.github.gv2011.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;

public final class Pair<K,V> extends SimpleEntry<K,V>{

  public Pair(final java.util.Map.Entry<? extends K, ? extends V> entry) {
    super(entry);
  }

  public Pair(final K key, final V value) {
    super(key, value);
  }

  @Override
  public V setValue(final V value) {
    throw new UnsupportedOperationException();
  }

  public static final <T> Comparator<Pair<T,T>> comparator(final Comparator<? super T> order){
    return comparator(order,order);
  }

  public static final <T extends Comparable<T>> Comparator<Pair<T,T>> comparator(){
    return comparator(Comparator.naturalOrder());
  }

  public static final <K,V> Comparator<Pair<K,V>> comparator(
    final Comparator<? super K> keyOrder, final Comparator<? super V> valueOrder
  ){
    return (p1,p2)->{
      final int c1 = keyOrder.compare(p1.getKey(), p2.getKey());
      return c1!=0? c1 : valueOrder.compare(p1.getValue(), p2.getValue());
    };
  }

}
