package com.github.gv2011.util.json.imp;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
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

import static com.github.gv2011.util.Verify.verify;

import java.util.stream.Stream;

import com.github.gv2011.util.Nothing;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;
import com.github.gv2011.util.json.JsonNull;
import com.github.gv2011.util.json.JsonWriter;


final class JsonNullImp extends AbstractJsongNode implements JsongNode, JsonNull{

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
