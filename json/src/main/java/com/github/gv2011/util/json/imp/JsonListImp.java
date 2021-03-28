package com.github.gv2011.util.json.imp;

import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.json.JsonNode;

final class JsonListImp extends AbstractJsonList{

  private final IList<JsonNode> entries;

  JsonListImp(final JsonFactoryImp f, final IList<JsonNode> entries) {
    super(f);
    this.entries = entries;
  }

  @Override
  public int size() {
    return entries.size();
  }

  @Override
  public JsonNode get(final int index) {
    return entries.get(index);
  }

}
