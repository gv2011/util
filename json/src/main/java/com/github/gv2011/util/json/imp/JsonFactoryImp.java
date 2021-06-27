package com.github.gv2011.util.json.imp;

import java.io.Reader;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
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

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

import com.github.gv2011.util.ServiceLoaderUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.icol.ICollectionFactory;
import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.json.Adapter;
import com.github.gv2011.util.json.JsonBoolean;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonList;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNull;
import com.github.gv2011.util.json.JsonNumber;
import com.github.gv2011.util.json.JsonObject;
import com.github.gv2011.util.json.JsonReader;
import com.github.gv2011.util.json.JsonString;
import com.github.gv2011.util.json.JsonWriter;
import com.github.gv2011.util.num.Decimal;


public final class JsonFactoryImp implements JsonFactory{

  private static final ICollectionFactory ICOLF = ICollections.iCollections();

  private final Adapter adapter;

  final JsonNullImp jsonNull;
  final AbstractJsonList emptyList;
  final ISet<Characteristics> listCharacteristics = ICOLF.emptySet();
  final ISet<Characteristics> mapCharacteristics = ICOLF.setOf(Characteristics.UNORDERED);

  public JsonFactoryImp() {
    this(ServiceLoaderUtils.loadService(Adapter.class));
  }

  public JsonFactoryImp(final Adapter adapter) {
    this.adapter = adapter;
    jsonNull = new JsonNullImp(this);
    emptyList = createEmptyList();
  }

  @Override
  public JsonNode deserialize(final String json) {
    return adapter.deserialize(this, json);
  }

  @Override
  public JsonReader jsonReader(final Reader in) {
	return adapter.newJsonReader(this, in);
  }

  @Override
  public JsonWriter jsonWriter(final Writer writer) {
  	return adapter.newJsonWriter(writer);
  }

  @Override
  public Collector<JsonNode, ?, JsonList> toJsonList() {
    return new JsonListCollector(this);
  }

  @Override
  public JsonNode emptyList() {
    return emptyList;
  }

  @Override
  public <T> Collector<T, ?, JsonObject> toJsonObject(
    final Function<? super T, String> keyMapper,
    final Function<? super T, JsonNode> valueMapper
  ) {
    return new JsonObjectCollector<>(this, keyMapper, valueMapper);
  }



  @Override
  public Collector<Entry<String, JsonNode>, ?, JsonObject> toJsonObject() {
    return toJsonObject(Entry::getKey, Entry::getValue);
  }

  ICollectionFactory iCollections() {return ICOLF;}

  @Override
  public JsonNull jsonNull() {
    return jsonNull;
  }

  @Override
  public JsonString primitive(final String s) {
    return new JsonStringImp(this, s);
  }

  @Override
  public JsonString primitive(final Bytes b) {
    return primitive(b.toBase64().utf8ToString());
  }

  @Override
  public JsonNumber primitive(final Decimal number) {
    return new JsonNumberImp(this, number);
  }

  @Override
  public JsonBoolean primitive(final boolean b) {
    return new JsonBooleanImp(this, b);
  }

  @Override
public String serialize(final JsonNode e) {
    final StringWriter out = new StringWriter();
    final JsonWriter jsonWriter = adapter.newJsonWriter(out);
    e.write(jsonWriter);
    jsonWriter.flush();
    return out.toString();
  }

  @Override
  public JsonList asJsonList(final IList<?> list, final Function<Object, JsonNode> converter) {
    return list.isEmpty() ? emptyList :new JsonListWrapper<Object>(this, list, o->(JsonNode)converter.apply(o));
  }

  private AbstractJsonList createEmptyList() {
    return new AbstractJsonList(this){
      @Override
      public JsonNode get(final int index) {
        throw new IndexOutOfBoundsException();
      }
      @Override
      public int size() {
        return 0;
      }
    };
  }


}
