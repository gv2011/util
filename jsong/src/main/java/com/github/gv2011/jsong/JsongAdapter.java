package com.github.gv2011.jsong;

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;

import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Iterator;

import com.github.gv2011.gson.stream.JsonReader;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.ex.ThrowingFunction;
import com.github.gv2011.util.json.Adapter;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonList;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonObject;
import com.github.gv2011.util.json.JsonWriter;

public final class JsongAdapter implements Adapter{

  @Override
  public JsonWriter newJsonWriter(final Writer out, final boolean compact) {
    final com.github.gv2011.gson.stream.JsonWriter delegate = new com.github.gv2011.gson.stream.JsonWriter(out);
    delegate.setIndent(compact ? "" : "  ");
    delegate.setSerializeNulls(false);
    return new JsongWriter(delegate);
  }

  @Override
  public com.github.gv2011.util.json.JsonReader newJsonReader(final JsonFactory jf, final Reader in) {
    return new JsongReader(jf, new JsonReader(in));
  }

  @Override
  public JsonNode deserialize(final JsonFactory jf, final String json) {
    return callWithCloseable(()->new JsonReader(new StringReader(json)),
      (ThrowingFunction<JsonReader,JsonNode>)r->deserialize(jf, r)
    );
  }

  private JsonNode deserialize(final JsonFactory jf, final JsonReader in) {
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


  private final class It implements Iterator<JsonNode> {
    private final JsonReader in;
    private final JsonFactory jf;

    private It(final JsonFactory jf, final JsonReader in) {
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


  private final class Itm implements Iterator<Pair<String,JsonNode>> {
    private final JsonReader in;
    private final JsonFactory jf;

    private Itm(final JsonFactory jf, final JsonReader in) {
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
