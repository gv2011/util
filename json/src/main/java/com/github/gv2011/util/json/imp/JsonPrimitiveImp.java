package com.github.gv2011.util.json.imp;

import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.util.stream.Stream;

import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonPrimitive;
import com.github.gv2011.util.num.Decimal;

abstract class JsonPrimitiveImp<P> extends AbstractJsongNode implements JsonPrimitive<P> {

  private final JsonFactoryImp f;
  final P value;

  JsonPrimitiveImp(final JsonFactoryImp f, final P value) {
    this.f = f;
    this.value = value;
  }

  @Override
  public String serialize() {
    return f.serialize(this);
  }

  @Override
  public P value() {
    return value;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <P2> P2 value(final Class<P2> primitiveClass) {
    if(value.getClass().equals(primitiveClass)) return (P2) value;
    else throw notYetImplementedException(value.getClass().getName());
  }

  @Override
  public boolean equals(final Object o) {
    if(this==o) return true;
    else if(o instanceof JsonPrimitive) {
      return ((JsonPrimitive<?>)o).value().equals(value);
    }
    else return false;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public JsonPrimitive<P> filter(final String attribute) {
    return this;
  }

  @Override
  public Stream<JsonNode> stream() {
    return Stream.of(this);
  }

  @Override
  public String asString() {
    if(value instanceof String) return value.toString();
    else return super.asString();
  }

  @Override
  public Decimal asNumber() {
    if(value instanceof Decimal) return (Decimal) value;
    else return super.asNumber();
  }

    @Override
    public final String rawToString() {
        return value.toString();
    }

}
