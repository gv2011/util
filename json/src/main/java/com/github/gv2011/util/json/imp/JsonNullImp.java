package com.github.gv2011.util.json.imp;

import static com.github.gv2011.util.Verify.verify;

import java.util.stream.Stream;

import com.github.gv2011.util.icol.Nothing;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;
import com.github.gv2011.util.json.JsonNull;
import com.github.gv2011.util.json.JsonWriter;


final class JsonNullImp extends AbstractJsongNode implements JsonNull{

  private final JsonFactoryImp f;

  JsonNullImp(final JsonFactoryImp f) {
    this.f = f;
  }

  @Override
  public String serialize() {
    return f.serialize(this);
  }

  @Override
  public boolean equals(final Object o) {
    if(this==o) return true;
    else return o instanceof JsonNull;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public String toString() {
    return JsonNodeType.NULL.toString();
  }

  @Override
  public void write(final JsonWriter out) {
    out.nullValue();
  }

  @Override
  public JsonNull filter(final String attribute) {
    return this;
  }

  @Override
  public Stream<JsonNode> stream() {
    return Stream.empty();
  }


  @Override
  public JsonNodeType jsonNodeType() {
    return JsonNodeType.NULL;
  }

  @Override
  public Nothing value() {
    return Nothing.INSTANCE;
  }

  @Override
  public <P2> P2 value(final Class<P2> primitiveClass) {
    verify(primitiveClass.equals(Nothing.class));
    return primitiveClass.cast(Nothing.INSTANCE);
  }

  @Override
  public int compareWithOtherOfSameJsonNodeType(final JsonNode o) {
    return 0;
  }

  @Override
  public JsonNull asNull() {
    return this;
  }

  @Override
  public String rawToString() {
    return "null";
  }

  @Override
  public boolean isNull() {
    return true;
  }

}
