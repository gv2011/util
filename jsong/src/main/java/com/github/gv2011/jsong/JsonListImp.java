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




import java.io.IOException;

import com.github.gv2011.util.icol.AbstractIList;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.json.JsonList;
import com.github.gv2011.util.json.JsonNode;
import com.google.gson.stream.JsonWriter;

final class JsonListImp extends AbstractIList<JsonNode> implements JsongNode, JsonList{

  private final JsonFactoryImp f;
  private final IList<JsongNode> entries;

  JsonListImp(final JsonFactoryImp f, final IList<JsongNode> entries) {
    this.f = f;
    this.entries = entries;
  }

  @Override
  public String serialize() {
    return f.serialize(this);
  }

  @Override
  public int size() {
    return entries.size();
  }

  @Override
  public JsonNode get(final int index) {
    return entries.get(index);
  }

  @Override
  public void write(final JsonWriter out) throws IOException {
    out.beginArray();
    for(final JsongNode e : entries) e.write(out);
    out.endArray();
  }

}
