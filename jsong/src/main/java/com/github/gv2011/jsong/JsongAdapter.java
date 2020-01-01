package com.github.gv2011.jsong;

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;

import java.io.StringReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Iterator;

import com.github.gv2011.gson.stream.JsonReader;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.ex.ThrowingFunction;
import com.github.gv2011.util.json.JsonList;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonObject;
import com.github.gv2011.util.json.JsonWriter;
import com.github.gv2011.util.json.imp.Adapter;
import com.github.gv2011.util.json.imp.JsonFactoryImp;

public final class JsongAdapter implements Adapter{

  @Override
  @SuppressWarnings("resource")
  public JsonWriter newJsonWriter(final Writer out) {
    final com.github.gv2011.gson.stream.JsonWriter delegate = new com.github.gv2011.gson.stream.JsonWriter(out);
    delegate.setIndent("  ");
    delegate.setSerializeNulls(false);
    return new JsongWriter(delegate);
  }

  @Override
  public JsonNode deserialize(final JsonFactoryImp jf, final String json) {
    return callWithCloseable(()->new JsonReader(new StringReader(json)),
    	(ThrowingFunction<JsonReader,JsonNode>)r->deserialize(jf, r)
    );
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


  private final class It implements Iterator<JsonNode> {
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


  private final class Itm implements Iterator<Pair<String,JsonNode>> {
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
