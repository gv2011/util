package com.github.gv2011.util.icol.guava;

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
