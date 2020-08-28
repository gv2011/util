package com.github.gv2011.jsong;

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;

import java.math.BigDecimal;
import java.util.Iterator;

import com.github.gv2011.gson.stream.JsonReader;
import com.github.gv2011.util.Nothing;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonBoolean;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonList;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;
import com.github.gv2011.util.json.JsonNull;
import com.github.gv2011.util.json.JsonNumber;
import com.github.gv2011.util.json.JsonObject;
import com.github.gv2011.util.json.JsonPrimitive;
import com.github.gv2011.util.json.JsonString;

final class JsongReader implements com.github.gv2011.util.json.JsonReader {

  private static final Opt<JsonNodeType>[] TYPE_MAP = null;

  private final JsonFactory jf;
  private final JsonReader delegate;

  JsongReader(final JsonFactory jf, JsonReader delegate) {
    this.jf = jf;
    this.delegate = delegate;
  }

  @Override
  public void close() {
    call(delegate::close);
  }

  @Override
  public void readArrayStart() {
    call(delegate::beginArray);
  }

  @Override
  public void readArrayEnd() {
    call(delegate::endArray);
  }

  @Override
  public void readObjectStart() {
    call(delegate::beginObject);
  }

  @Override
  public void readObjectEnd() {
    call(delegate::endObject);
  }

  @Override
  public boolean hasNext() {
    return call(delegate::hasNext);
  }

  @Override
  public Opt<JsonNodeType> nextType() {
    return TYPE_MAP[call(delegate::peek).ordinal()];
  }

  @Override
  public String readName() {
    return call(delegate::nextName);
  }

  public JsonString readString() {
    return jf.primitive(call(delegate::nextString));
  }
  
  public JsonNumber readNumber() {
    return jf.primitive(new BigDecimal(call(delegate::nextString)));
  }
  
  public JsonBoolean readBoolean() {
    return jf.primitive(call(delegate::nextBoolean));
  }
  
  public JsonNull readNull() {
    call(delegate::nextNull);
    return jf.jsonNull();
  }
  
  @Override
  public JsonNode readNode() {
    return call(() -> {
      switch (delegate.peek()) {
      case BEGIN_ARRAY:
        delegate.beginArray();
        final JsonList list = XStream.fromIterator(new It()).collect(jf.toJsonList());
        delegate.endArray();
        return list;
      case BEGIN_OBJECT:
        delegate.beginObject();
        final JsonObject obj = XStream.fromIterator(new Itm()).collect(jf.toJsonObject());
        delegate.endObject();
        return obj;
      default:
        return readPrimitive();
      }
    });
  }

  @Override
  public JsonList readList() {
    call(delegate::beginArray);
    final JsonList list = XStream.fromIterator(new It()).collect(jf.toJsonList());
    call(delegate::endArray);
    return list;
  }

  @Override
  public JsonObject readObject() {
    call(delegate::beginObject);
    final JsonObject obj = XStream.fromIterator(new Itm()).collect(jf.toJsonObject());
    call(delegate::endObject);
    return obj;
  }

  @Override
  public JsonPrimitive<?> readPrimitive() {
    return call(() -> {
      switch (delegate.peek()) {
      case STRING:
        return readString();
      case NUMBER:
        return readNumber();
      case BOOLEAN:
        return readBoolean();
      case NULL:
        return readNull();
      default:
        throw new IllegalArgumentException();
      }
    });
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public <P> JsonPrimitive<P> readPrimitive(Class<P> clazz) {
    return call(() -> {
      switch (delegate.peek()) {
      case STRING:
        verifyEqual(clazz, String.class);
        return (JsonPrimitive) readString();
      case NUMBER:
        verifyEqual(clazz, BigDecimal.class);
        return readNumber();
      case BOOLEAN:
        verifyEqual(clazz, Boolean.class);
        return readBoolean();
      case NULL:
        verifyEqual(clazz, Nothing.class);
        return readNull();
      default:
        throw new IllegalArgumentException();
      }
    });
  }

  
  private final class It implements Iterator<JsonNode> {
    @Override
    public boolean hasNext() {
        return call(delegate::hasNext);
    }
    @Override
    public JsonNode next() {
        return readNode();
    }
  }

  private final class Itm implements Iterator<Pair<String,JsonNode>> {
    @Override
    public boolean hasNext() {
        return call(delegate::hasNext);
    }
    @Override
    public Pair<String,JsonNode> next() {
        final String key = call(delegate::nextName);
        final JsonNode value = readNode();
        return pair(key, value);
    }
  }
}
