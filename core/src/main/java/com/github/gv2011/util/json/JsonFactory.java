package com.github.gv2011.util.json;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
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
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collector;

import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.icol.IList;

public interface JsonFactory {

  JsonNode deserialize(String json);

  JsonList asJsonList(IList<?> list, Function<Object,JsonNode> converter);

  Collector<JsonNode,?,JsonList> toJsonList();

  <T> Collector<T, ?, JsonObject> toJsonObject(
    final Function<? super T, String> keyMapper,
    final Function<? super T, JsonNode> valueMapper
  );

  JsonNode emptyList();


  Collector<Entry<String,JsonNode>, ?, JsonObject> toJsonObject();

  JsonNull jsonNull();

  JsonString primitive(String s);

  JsonString primitive(Bytes b);

  JsonNumber primitive(int number);

  JsonNumber primitive(long number);

  JsonNumber primitive(BigDecimal number);

  JsonBoolean primitive(boolean b);

  JsonBoolean primitive(Boolean b);

}
