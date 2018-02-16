package com.github.gv2011.util.json;

/*-
 * #%L
 * util-test
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
import static com.github.gv2011.testutil.Matchers.is;
import static com.github.gv2011.testutil.Matchers.isA;
import static com.github.gv2011.testutil.Matchers.meets;
import static com.github.gv2011.util.CollectionUtils.listOf;
import static com.github.gv2011.util.CollectionUtils.pair;
import static org.junit.Assert.assertThat;

import java.util.stream.Stream;

import org.junit.Test;

import com.github.gv2011.util.CollectionUtils;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.ResourceUtils;

public class JsonFactoryTest {

  private final JsonFactory jf = JsonUtils.jsonFactory();

  @Test
  public void testDeserialize() {
    JsonNode n = jf.deserialize("{}");
    assertThat(n, isA(JsonObject.class));

    n = jf.deserialize("{\"k\":\"str\"}");
    assertThat(n, isA(JsonObject.class));
    final JsonObject obj = (JsonObject)n;
    assertThat(obj, meets(o->o.size()==1));
    assertThat(obj.get("k").asString(), is("str"));
  }

  @Test
  public void testDeserializeExample() {
    final String json = ResourceUtils.getTextResource(JsonFactoryTest.class, JsonFactoryTest.class.getSimpleName()+".json");
    final JsonNode node = jf.deserialize(json);
    assertThat(node, isA(JsonNode.class));
  }

  @Test
  public void testToJsonList() {
    final JsonNode n1 = jf.primitive("a");
    final JsonNode n2 = jf.primitive(1);
    final JsonList list = Stream.of(n1,n2).collect(jf.toJsonList());
    assertThat(list, is(listOf(n1,n2)));
  }

  @Test
  public void testToJsonObject() {
    final String k1 = "k1";
    final String k2 = "k2";
    final JsonNode v1 = jf.primitive(434321415414543145L);
    final JsonNode v2 = jf.primitive(true);
    final JsonObject obj = Stream.of(pair(k1,v1), pair(k2,v2)).collect(jf.toJsonObject(
      Pair::getKey,
      Pair::getValue
    ));
    CollectionUtils.<String,JsonNode>sortedMapBuilder().put(k1, v1).build();
    assertThat(
      obj,
      is(
        CollectionUtils.<String,JsonNode>sortedMapBuilder()
        .put(k1, v1)
        .put(k2, v2)
        .build()
      )
    );
  }

}
