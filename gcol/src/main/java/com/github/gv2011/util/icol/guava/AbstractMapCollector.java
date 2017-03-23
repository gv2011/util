package com.github.gv2011.util.icol.guava;

import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.MapBuilder;

public abstract class AbstractMapCollector<M extends IMap<K,V>, K, V, B extends MapBuilder<M,K,V,B>,T>
implements Collector<T, B, M> {

  private final Function<? super T, ? extends K> keyMapper;
  private final Function<? super T, ? extends V> valueMapper;

  AbstractMapCollector(
    final Function<? super T, ? extends K> keyMapper,
    final Function<? super T, ? extends V> valueMapper
  ) {
    this.keyMapper = keyMapper;
    this.valueMapper = valueMapper;
  }

  @Override
  public BiConsumer<B, T> accumulator() {
    return (b,t)->{
      final K key = keyMapper.apply(t);
      final V value = valueMapper.apply(t);
      synchronized(b){b.put(key, value);}
    };
  }

  @Override
  public BinaryOperator<B> combiner() {
    return (b1,b2)->{
      final M m2;
      synchronized(b2){ m2 = b2.build();}
      synchronized(b1){return b1.putAll(m2);}
    };
  }

  @Override
  public Function<B, M> finisher() {
    return b->{synchronized(b){return b.build();}};
  }

}
