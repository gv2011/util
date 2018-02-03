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



import static com.github.gv2011.util.ex.Exceptions.call;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

import com.github.gv2011.util.CollectionUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.icol.ICollectionFactory;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonList;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonObject;
import com.github.gv2011.util.json.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public final class JsonFactoryImp implements JsonFactory{

  private static final ICollectionFactory ICOLF = CollectionUtils.iCollections();

  private final Adapter adapter;

  final JsonNullImp jsonNull;
  final ISet<Characteristics> listCharacteristics = ICOLF.emptySet();
  final ISet<Characteristics> mapCharacteristics = ICOLF.setOf(Characteristics.UNORDERED);

  public JsonFactoryImp() {
    adapter = new Adapter(this);
    jsonNull = new JsonNullImp(this);
  }

  @Override
  public JsonNode deserialize(final String json) {
    return call(()->adapter.read(new JsonReader(new StringReader(json))));
  }

  @Override
  public Collector<JsonNode, ?, JsonList> toJsonList() {
    return new JsonListCollector(this);
  }

  @Override
  public <T> Collector<T, ?, JsonObject> toJsonObject(
    final Function<? super T, String> keyMapper,
    final Function<? super T, JsonNode> valueMapper
  ) {
    return new JsonObjectCollector<>(this, keyMapper, valueMapper);
  }

  ICollectionFactory iCollections() {return ICOLF;}

  @Override
  public JsonNullImp jsonNull() {
    return jsonNull;
  }

  @Override
  public JsonPrimitive<String> primitive(final String s) {
    return new JsonPrimitiveImp<>(this, s);
  }

  @Override
  public JsonPrimitive<String> primitive(final Bytes b) {
    return primitive(b.toBase64().utf8ToString());
  }

  @Override
  public JsonPrimitive<BigDecimal> primitive(final int number) {
    return primitive(BigDecimal.valueOf(number));
  }

  @Override
  public JsonPrimitive<BigDecimal> primitive(final long number) {
    return primitive(BigDecimal.valueOf(number));
  }

  @Override
  public JsonPrimitive<BigDecimal> primitive(final BigDecimal number) {
    return new JsonPrimitiveImp<>(this, number);
  }

  @Override
  public JsonPrimitive<Boolean> primitive(final boolean b) {
    return primitive(Boolean.valueOf(b));
  }

  @Override
  public JsonPrimitive<Boolean> primitive(final Boolean b) {
    return new JsonPrimitiveImp<>(this, b);
  }

  String serialize(final JsongNode e) {
    final StringWriter out = new StringWriter();
    final JsonWriter jsonWriter = new JsonWriter(out);
    jsonWriter.setIndent("  ");
    jsonWriter.setSerializeNulls(false);
    call(()->adapter.write(jsonWriter, e));
    return out.toString();
  }

}
