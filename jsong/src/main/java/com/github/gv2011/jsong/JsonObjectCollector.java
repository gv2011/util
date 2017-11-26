package com.github.gv2011.jsong;

/*-
 * #%L
 * jsong
 * %%
 * Copyright (C) 2017 Vinz (https://github.com/gv2011)
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



import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedMap.Builder;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonObject;

final class JsonObjectCollector<T> implements Collector<T, ISortedMap.Builder<String, JsongNode>, JsonObject> {

  private final JsonFactoryImp f;
  private final Function<? super T, String> keyMapper;
  private final Function<? super T, JsonNode> valueMapper;

  JsonObjectCollector(
    final JsonFactoryImp f,
    final Function<? super T, String> keyMapper,
    final Function<? super T, JsonNode> valueMapper
  ) {
    this.f = f;
    this.keyMapper = keyMapper;
    this.valueMapper = valueMapper;
  }

  @Override
  public BiConsumer<Builder<String, JsongNode>, T> accumulator() {
    return (b,e)->b.put(keyMapper.apply(e), (JsongNode)valueMapper.apply(e));
  }

  @Override
  public Set<Characteristics> characteristics() {
    return f.mapCharacteristics;
  }

  @Override
  public BinaryOperator<Builder<String, JsongNode>> combiner() {
    return (b1,b2)->b1.putAll(b2.build());
  }

  @Override
  public Function<Builder<String, JsongNode>, JsonObject> finisher() {
    return b->new JsonObjectImp(f, b.build());
  }

  @Override
  public Supplier<Builder<String, JsongNode>> supplier() {
    return f.iCollections()::sortedMapBuilder;
  }

}
