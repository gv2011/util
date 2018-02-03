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




import static com.github.gv2011.util.CollectionUtils.upcast;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.gv2011.util.icol.AbstractISortedMap;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.json.JsonList;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonObject;
import com.google.gson.stream.JsonWriter;

final class JsonObjectImp extends AbstractISortedMap<String,JsonNode> implements JsongNode, JsonObject{

  private final JsonFactoryImp f;
  private final ISortedMap<String,JsongNode> entries;

  JsonObjectImp(final JsonFactoryImp f, final ISortedMap<String,JsongNode> entries) {
    this.f = f;
    this.entries = entries;
  }

  @Override
  public String serialize() {
    return f.serialize(this);
  }

  @Override
  public ISortedSet<String> keySet() {
    return entries.keySet();
  }

  @Override
  public Optional<JsonNode> tryGet(final Object key) {
    return upcast(entries.tryGet(key));
  }

  @Override
  public void write(final JsonWriter out) throws IOException {
    out.beginObject();
    for (final Entry<String, JsongNode> e : entries.entrySet()) {
      out.name(e.getKey());
      e.getValue().write(out);
    }
    out.endObject();
  }

  @Override
  public JsonNode filter(final String attribute) {
    return entries.tryGet(attribute).orElse(f.jsonNull);
    //    return entries.entrySet().stream()
    //      .filter(e->e.getKey().equals(attribute))
    //      .map(e->pair(e.getKey(), e.getValue().filter(attribute)))
    //      .collect(f.toJsonObject(Pair::getKey, Pair::getValue))
    //    ;
  }

  @Override
  public Stream<JsonNode> stream() {
    return entries.entrySet().stream().map(e->{
      final ISortedMap.Builder<String, JsongNode> b = f.iCollections().sortedMapBuilder();
      b.put("key", (JsongNode)f.primitive(e.getKey()));
      b.put("value", e.getValue());
      return new JsonObjectImp(f, b.build());
    });
  }

  @Override
  public JsonList asList() {
    return AbstractJsongNode.asList(this);
  }

  @Override
  public String asString() {
    return AbstractJsongNode.asString(this);
  }

  @Override
  public BigDecimal asNumber() {
    return AbstractJsongNode.asNumber(this);
  }


}
