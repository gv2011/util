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
import java.math.BigDecimal;

import com.github.gv2011.util.NumUtils;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;
import com.github.gv2011.util.json.JsonNumber;
import com.github.gv2011.util.json.JsonWriter;

public class JsonNumberImp extends JsonPrimitiveImp<BigDecimal> implements JsonNumber{

  JsonNumberImp(final JsonFactoryImp f, final BigDecimal value) {
    super(f, NumUtils.canonical(value));
  }

  @Override
  public JsonNodeType jsonNodeType() {
    return JsonNodeType.NUMBER;
  }

  @Override
  public int compareWithOtherOfSameJsonNodeType(final JsonNode o) {
    return value.subtract(o.asNumber()).signum();
  }

  @Override
  public void write(final JsonWriter out){
    out.writeNumber(value);
  }

}
