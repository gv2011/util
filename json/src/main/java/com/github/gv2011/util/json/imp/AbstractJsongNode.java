package com.github.gv2011.util.json.imp;

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
import static com.github.gv2011.util.ex.Exceptions.format;

import java.math.BigDecimal;

import com.github.gv2011.util.json.JsonList;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;
import com.github.gv2011.util.json.JsonNull;
import com.github.gv2011.util.json.JsonObject;

abstract class AbstractJsongNode implements JsongNode{

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
  public BigDecimal asNumber() {
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

  static final int compare(final JsongNode n1, final JsonNode n2) {
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

  static final JsongNode toJsongNode(final JsonNode n) {
    return (JsongNode) n;
  }

  static final JsonObject asObject(final JsongNode n) {
    throw new ClassCastException(format("{} is not a JsonObject (actual class is {}).", n, n.getClass()));
  }

  static final JsonNull asNull(final JsongNode n) {
    throw new ClassCastException(format("{} is not a JsonNull (actual class is {}).", n, n.getClass()));
  }

  static final JsonList asList(final JsongNode n) {
    throw new ClassCastException(format("{} is not a JsonList (actual class is {}).", n, n.getClass()));
  }

  static final boolean asBoolean(final JsongNode n) {
    throw new ClassCastException(format("{} is not a Boolean node (actual class is {}).", n, n.getClass()));
  }

  static final String asString(final JsongNode n) {
    throw new ClassCastException(format("{} is not a String node (actual class is {}).", n, n.getClass()));
  }

  static final BigDecimal asNumber(final JsongNode n) {
    throw new ClassCastException(format("{} is not a Number node (actual class is {}).", n, n.getClass()));
  }

  static final String toString(final JsongNode n) {
    return n.jsonNodeType()+"("+n.rawToString()+")";
  }

}
