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




import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;

import com.github.gv2011.util.Comparison;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.icol.AbstractIList;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.json.JsonList;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;
import com.github.gv2011.util.json.JsonNull;
import com.github.gv2011.util.json.JsonObject;
import com.google.gson.stream.JsonWriter;

final class JsonListImp extends AbstractIList<JsonNode> implements JsongNode, JsonList{

  private static final Comparator<JsonList> LIST_COMPARATOR = Comparison.listComparator();

  private final JsonFactoryImp f;
  private final IList<JsongNode> entries;

  JsonListImp(final JsonFactoryImp f, final IList<JsongNode> entries) {
    this.f = f;
    this.entries = entries;
  }

  @Override
  public int compareTo(final JsonNode o) {
    return AbstractJsongNode.compare(this,o);
  }


  @Override
  public boolean asBoolean() {
    return AbstractJsongNode.asBoolean(this);
  }


  @Override
  public JsonNodeType jsonNodeType() {
    return JsonNodeType.LIST;
  }


  @Override
  public String serialize() {
    return f.serialize(this);
  }

  @Override
  public int size() {
    return entries.size();
  }

  @Override
  public JsonNode get(final int index) {
    return entries.get(index);
  }

  @Override
  public void write(final JsonWriter out) throws IOException {
    out.beginArray();
    for(final JsongNode e : entries) e.write(out);
    out.endArray();
  }

  @Override
  public JsonList filter(final String attribute) {
    return stream().map(n->n.filter(attribute)).collect(f.toJsonList());
  }

  @Override
  public XStream<JsonNode> stream() {
    return super.stream();
  }

  @Override
  public JsonList asList() {
    return this;
  }

  @Override
  public String asString() {
    return AbstractJsongNode.asString(this);
  }

  @Override
  public BigDecimal asNumber() {
    return AbstractJsongNode.asNumber(this);
  }

  @Override
  public JsonObject asObject() {
    return AbstractJsongNode.asObject(this);
  }

  @Override
  public JsonNull asNull() {
    return AbstractJsongNode.asNull(this);
  }

  @Override
  public int compareWithOtherOfSameJsonNodeType(final JsonNode o) {
    return LIST_COMPARATOR.compare(this, o.asList());
  }

}
