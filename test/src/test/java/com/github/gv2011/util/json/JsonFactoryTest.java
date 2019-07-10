package com.github.gv2011.util.json;

import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Matchers.is;
import static com.github.gv2011.testutil.Matchers.isA;
import static com.github.gv2011.testutil.Matchers.meets;
import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.icol.ICollections.listOf;

import java.util.stream.Stream;

import org.junit.Test;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.ResourceUtils;
import com.github.gv2011.util.icol.ICollections;

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
    ICollections.<String,JsonNode>sortedMapBuilder().put(k1, v1).build();
    assertThat(
      obj,
      is(
        ICollections.<String,JsonNode>sortedMapBuilder()
        .put(k1, v1)
        .put(k2, v2)
        .build()
      )
    );
  }

}
