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



import static com.github.gv2011.util.ex.Exceptions.notYetImplemented;
import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.stream.Stream;

import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

abstract class JsonPrimitiveImp<P> extends AbstractJsongNode implements JsongNode, JsonPrimitive<P> {

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
  public String toString() {
    return value.toString();
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
  public void write(final JsonWriter out) throws IOException {
    if (value instanceof Number)       out.value((Number)value);
    else if (value instanceof Boolean) out.value(((Boolean)value).booleanValue());
    else if (value instanceof String)  out.value((String)value);
    else notYetImplemented(value.getClass().getName());
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
  public BigDecimal asNumber() {
    if(value instanceof BigDecimal) return (BigDecimal) value;
    else return super.asNumber();
  }

}
