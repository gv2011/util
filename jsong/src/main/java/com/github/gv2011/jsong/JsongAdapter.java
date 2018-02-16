package com.github.gv2011.jsong;

/*-
 * #%L
 * jsong
 * %%
 * Copyright (C) 2017 - 2018 Vinz (https://github.com/gv2011)
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
import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.ex.Exceptions.call;

import java.io.StringReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Iterator;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.json.JsonList;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonObject;
import com.github.gv2011.util.json.JsonWriter;
import com.github.gv2011.util.json.imp.Adapter;
import com.github.gv2011.util.json.imp.JsonFactoryImp;
import com.google.gson.stream.JsonReader;

public class JsongAdapter implements Adapter{

    @Override
    public JsonWriter newJsonWriter(final Writer out) {
        final com.google.gson.stream.JsonWriter delegate = new com.google.gson.stream.JsonWriter(out);
        delegate.setIndent("  ");
        delegate.setSerializeNulls(false);
        return new JsongWriter(delegate);
    }

    @Override
    public JsonNode deserialize(final JsonFactoryImp jf, final String json) {
        final JsonReader in = new JsonReader(new StringReader(json));
        return deserialize(jf, in);
    }

    private JsonNode deserialize(final JsonFactoryImp jf, final JsonReader in) {
      return call(()->{
        switch (in.peek()) {
        case STRING:
          return jf.primitive(in.nextString());
        case NUMBER:
          return jf.primitive(new BigDecimal(in.nextString()));
        case BOOLEAN:
          return jf.primitive(in.nextBoolean());
        case NULL:
          in.nextNull();
          return jf.jsonNull();
        case BEGIN_ARRAY:
          in.beginArray();
          final JsonList list = XStream.fromIterator(new It(jf, in)).collect(jf.toJsonList());
          in.endArray();
          return list;
        case BEGIN_OBJECT:
          in.beginObject();
          final JsonObject obj = XStream.fromIterator(new Itm(jf, in)).collect(jf.toJsonObject());
          in.endObject();
          return obj;
        case NAME:
        case END_DOCUMENT:
        case END_OBJECT:
        case END_ARRAY:
        default:
          throw new IllegalArgumentException();
        }
      });
    }


    private class It implements Iterator<JsonNode> {

        private final JsonReader in;
        private final JsonFactoryImp jf;

        private It(final JsonFactoryImp jf, final JsonReader in) {
            this.jf = jf;
            this.in = in;
        }

        @Override
        public boolean hasNext() {
            return call(in::hasNext);
        }

        @Override
        public JsonNode next() {
            return deserialize(jf, in);
        }

    }

    private class Itm implements Iterator<Pair<String,JsonNode>> {

        private final JsonReader in;
        private final JsonFactoryImp jf;

        private Itm(final JsonFactoryImp jf, final JsonReader in) {
            this.jf = jf;
            this.in = in;
        }

        @Override
        public boolean hasNext() {
            return call(in::hasNext);
        }

        @Override
        public Pair<String,JsonNode> next() {
            final String key = call(in::nextName);
            final JsonNode value = deserialize(jf, in);
            return pair(key, value);
        }

    }


}
