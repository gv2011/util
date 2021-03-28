package com.github.gv2011.util.json.imp;

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

final class JsonObjectCollector<T> implements Collector<T, ISortedMap.Builder<String, JsonNode>, JsonObject> {

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
  public BiConsumer<Builder<String, JsonNode>, T> accumulator() {
    return (b,e)->b.put(keyMapper.apply(e), (JsonNode)valueMapper.apply(e));
  }

  @Override
  public Set<Characteristics> characteristics() {
    return f.mapCharacteristics;
  }

  @Override
  public BinaryOperator<Builder<String, JsonNode>> combiner() {
    return (b1,b2)->b1.putAll(b2.build());
  }

  @Override
  public Function<Builder<String, JsonNode>, JsonObject> finisher() {
    return b->new JsonObjectImp(f, b.build());
  }

  @Override
  public Supplier<Builder<String, JsonNode>> supplier() {
    return f.iCollections()::sortedMapBuilder;
  }

}
