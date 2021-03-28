package com.github.gv2011.util.json.imp;

import static com.github.gv2011.util.icol.ICollections.toISortedSet;
import static com.github.gv2011.util.icol.ICollections.upcast;

import java.util.Comparator;
import java.util.stream.Stream;

import com.github.gv2011.util.Comparison;
import com.github.gv2011.util.icol.AbstractISortedMap;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonList;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;
import com.github.gv2011.util.json.JsonNull;
import com.github.gv2011.util.json.JsonObject;
import com.github.gv2011.util.json.JsonWriter;
import com.github.gv2011.util.num.Decimal;


final class JsonObjectImp extends AbstractISortedMap<String,JsonNode> implements JsonObject{

  private static final Comparator<Opt<JsonNode>> OPT_COMPARATOR = Comparison.optComparator();

  private final JsonFactoryImp f;
  private final ISortedMap<String,JsonNode> entries;

  JsonObjectImp(final JsonFactoryImp f, final ISortedMap<String,JsonNode> entries) {
    this.f = f;
    this.entries = entries;
  }

  @Override
  public String serialize() {
    return f.serialize(this);
  }

  @Override
  public ISortedSet<String> keySet() {
    return entries.keySet();
  }

  @Override
  public Opt<JsonNode> tryGet(final Object key) {
    return upcast(entries.tryGet(key));
  }

  @Override
  public void write(final JsonWriter out){
    out.beginObject();
    //For better readability, write simple values first:
    for (final Entry<String, JsonNode> e : entries.entrySet()) {
      final JsonNode value = e.getValue();
      if(isSimple(value)) writeEntry(out, e.getKey(), value);
    }
    for (final Entry<String, JsonNode> e : entries.entrySet()) {
      final JsonNode value = e.getValue();
      if(!isSimple(value)) writeEntry(out, e.getKey(), value);
    }
    out.endObject();
  }

  private void writeEntry(final JsonWriter out, final String key, final JsonNode value) {
    out.name(key);
    value.write(out);
  }

  private boolean isSimple(final JsonNode value) {
    final JsonNodeType nodeType = value.jsonNodeType();
    return !nodeType.equals(JsonNodeType.OBJECT) && !nodeType.equals(JsonNodeType.LIST);
  }

@Override
  public JsonNode filter(final String attribute) {
    return entries.tryGet(attribute).orElse(f.jsonNull);
    //    return entries.entrySet().stream()
    //      .filter(e->e.getKey().equals(attribute))
    //      .map(e->pair(e.getKey(), e.getValue().filter(attribute)))
    //      .collect(f.toJsonObject(Pair::getKey, Pair::getValue))
    //    ;
  }

  @Override
  public Stream<JsonNode> stream() {
    return entries.entrySet().stream().map(e->{
      final ISortedMap.Builder<String, JsonNode> b = f.iCollections().sortedMapBuilder();
      b.put("key", (JsonNode)f.primitive(e.getKey()));
      b.put("value", e.getValue());
      return new JsonObjectImp(f, b.build());
    });
  }

  @Override
  public JsonList asList() {
    return AbstractJsongNode.asList(this);
  }

  @Override
  public String asString() {
    return AbstractJsongNode.asString(this);
  }

  @Override
  public Decimal asNumber() {
    return AbstractJsongNode.asNumber(this);
  }

  @Override
  public JsonObject asObject() {
    return this;
  }

  @Override
  public JsonNull asNull() {
    return AbstractJsongNode.asNull(this);
  }

  @Override
  public int compareTo(final JsonNode o) {
    return AbstractJsongNode.compare(this, o);
  }

  @Override
  public boolean asBoolean() {
    return AbstractJsongNode.asBoolean(this);
  }

  @Override
  public JsonNodeType jsonNodeType() {
    return JsonNodeType.OBJECT;
  }

  @Override
  public int compareWithOtherOfSameJsonNodeType(final JsonNode o) {
      final JsonObject obj2 = o.asObject();
      return
        Stream.concat(
          keySet().parallelStream(),
          obj2.keySet().parallelStream()
        )
        .collect(toISortedSet()).stream()
        .mapToInt(k->OPT_COMPARATOR.compare(tryGet(k), obj2.tryGet(k)))
        .filter(i->i!=0)
        .findFirst().orElse(0)
      ;
  }

  @Override
  public String rawToString() {
    return super.toString();
  }

  @Override
  public String toString() {
    return AbstractJsongNode.toString(this);
  }

}
