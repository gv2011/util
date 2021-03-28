package com.github.gv2011.util.json.imp;

import java.util.function.Function;

import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.json.JsonNode;

final class JsonListWrapper<E> extends AbstractJsonList{

  private final IList<? extends E> entries;
  private final Function<? super E, ? extends JsonNode> mapper;

  JsonListWrapper(
    final JsonFactoryImp f,
    final IList<? extends E> entries,
    final Function<? super E, ? extends JsonNode> mapper
  ) {
    super(f);
    this.entries = entries;
    this.mapper = mapper;
  }

  @Override
  public int size() {
    return entries.size();
  }

  @Override
  public JsonNode get(final int index) {
    return mapper.apply(entries.get(index));
  }

}
