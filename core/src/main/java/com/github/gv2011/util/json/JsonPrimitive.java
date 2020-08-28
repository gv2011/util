package com.github.gv2011.util.json;

public interface JsonPrimitive<P> extends JsonNode{

  P value();

  <P2> P2 value(Class<P2> primitiveClass);

  @Override
  public JsonPrimitive<P> filter(final String attribute);

}
