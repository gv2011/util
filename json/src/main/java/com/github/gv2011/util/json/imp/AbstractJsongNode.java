package com.github.gv2011.util.json.imp;

import static com.github.gv2011.util.ex.Exceptions.format;

import com.github.gv2011.util.json.JsonList;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;
import com.github.gv2011.util.json.JsonNull;
import com.github.gv2011.util.json.JsonObject;
import com.github.gv2011.util.num.Decimal;

abstract class AbstractJsongNode implements JsonNode{

  @Override
  public JsonList asList() {
    return asList(this);
  }

  @Override
  public boolean asBoolean() {
    return asBoolean(this);
  }

  @Override
  public String asString() {
    return asString(this);
  }

  @Override
  public Decimal asNumber() {
    return asNumber(this);
  }

  @Override
  public JsonObject asObject() {
    return asObject(this);
  }

  @Override
  public JsonNull asNull() {
    return asNull(this);
  }

  @Override
  public final int compareTo(final JsonNode o) {
    return compare(this,o);
  }

  static final int compare(final JsonNode n1, final JsonNode n2) {
    final int result;
    if(n1==n2) result = 0;
    else {
      final JsonNodeType jsonNodeType = n1.jsonNodeType();
      final int tr = jsonNodeType.compareTo(n2.jsonNodeType());
      if(tr!=0) result = tr;
      else result = n1.compareWithOtherOfSameJsonNodeType(n2);
    }
    return result;
  }

  @Override
  public String toString() {
    return toString(this);
  }

  static final JsonNode toJsonNode(final JsonNode n) {
    return (JsonNode) n;
  }

  static final JsonObject asObject(final JsonNode n) {
    throw new ClassCastException(format("{} is not a JsonObject (actual class is {}).", n, n.getClass()));
  }

  static final JsonNull asNull(final JsonNode n) {
    throw new ClassCastException(format("{} is not a JsonNull (actual class is {}).", n, n.getClass()));
  }

  static final JsonList asList(final JsonNode n) {
    throw new ClassCastException(format("{} is not a JsonList (actual class is {}).", n, n.getClass()));
  }

  static final boolean asBoolean(final JsonNode n) {
    throw new ClassCastException(format("{} is not a Boolean node (actual class is {}).", n, n.getClass()));
  }

  static final String asString(final JsonNode n) {
    throw new ClassCastException(format("{} is not a String node (actual class is {}).", n, n.getClass()));
  }

  static final Decimal asNumber(final JsonNode n) {
    throw new ClassCastException(format("{} is not a Number node (actual class is {}).", n, n.getClass()));
  }

  static final String toString(final JsonNode n) {
    return n.jsonNodeType()+"("+n.rawToString()+")";
  }

}
