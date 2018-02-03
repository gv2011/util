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

import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISortedMap;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

final class Adapter extends TypeAdapter<JsongNode> {

  private final JsonFactoryImp f;

  Adapter(final JsonFactoryImp f) {
    this.f = f;
  }

  @Override
  public JsongNode read(final JsonReader in) throws IOException {
    switch (in.peek()) {
    case STRING:
      return new JsonPrimitiveImp<>(f, in.nextString());
    case NUMBER:
      final String number = in.nextString();
      return new JsonPrimitiveImp<>(f, new BigDecimal(number).stripTrailingZeros());
    case BOOLEAN:
      return new JsonPrimitiveImp<>(f, Boolean.valueOf(in.nextBoolean()));
    case NULL:
      in.nextNull();
      return f.jsonNull;
    case BEGIN_ARRAY:
      final IList.Builder<JsongNode> list = f.iCollections().listBuilder();
      in.beginArray();
      while (in.hasNext()) {
        list.add(read(in));
      }
      in.endArray();
      return new JsonListImp(f, list.build());
    case BEGIN_OBJECT:
      final ISortedMap.Builder<String,JsongNode> map = f.iCollections().sortedMapBuilder();
      in.beginObject();
      while (in.hasNext()) {
        map.put(in.nextName(), read(in));
      }
      in.endObject();
      return new JsonObjectImp(f, map.build());
    case NAME:
    case END_DOCUMENT:
    case END_OBJECT:
    case END_ARRAY:
    default:
      throw new IllegalArgumentException();
    }
  }

  @Override
  public void write(final JsonWriter out, final JsongNode value) throws IOException {
    value.write(out);
  }
}
