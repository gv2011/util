package com.github.gv2011.util.json.imp;

import java.util.Comparator;

import com.github.gv2011.util.Comparison;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.icol.AbstractIList;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.json.JsonList;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;
import com.github.gv2011.util.json.JsonNull;
import com.github.gv2011.util.json.JsonObject;
import com.github.gv2011.util.json.JsonWriter;
import com.github.gv2011.util.num.Decimal;


abstract class AbstractJsonList extends AbstractIList<JsonNode> implements JsonList{

  private static final Comparator<JsonList> LIST_COMPARATOR = Comparison.listComparator();

  private final JsonFactoryImp f;

  AbstractJsonList(final JsonFactoryImp f) {
    this.f = f;
  }

  @Override
  public int compareTo(final JsonNode o) {
    return AbstractJsongNode.compare(this,o);
  }


  @Override
  public boolean asBoolean() {
    return AbstractJsongNode.asBoolean(this);
  }


  @Override
  public JsonNodeType jsonNodeType() {
    return JsonNodeType.LIST;
  }


  @Override
  public String serialize(final boolean compact) {
    return f.serialize(this, compact);
  }

  @Override
  public abstract JsonNode get(int index);

  @Override
  public void write(final JsonWriter out){
    out.beginArray();
    for(int i=0; i<size(); i++) get(i).write(out);
    out.endArray();
  }

  @Override
  public JsonList filter(final String attribute) {
    return stream().map(n->n.filter(attribute)).collect(f.toJsonList());
  }

  @Override
  public XStream<JsonNode> stream() {
    return super.stream();
  }

  @Override
  public JsonList asList() {
    return this;
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
    return AbstractJsongNode.asObject(this);
  }

  @Override
  public JsonNull asNull() {
    return AbstractJsongNode.asNull(this);
  }

  @Override
  public int compareWithOtherOfSameJsonNodeType(final JsonNode o) {
    return LIST_COMPARATOR.compare(this, o.asList());
  }

  @Override
  public String rawToString() {
      return super.toString();
  }

  @Override
  public String toString() {
      return AbstractJsongNode.toString(this);
  }

  @Override
  public IList<JsonNode> reversed() {
    return new ReversedList();
  }

  private final class ReversedList extends AbstractJsonList{
    private ReversedList() {super(f);}
    @Override
    public JsonNode get(final int index) {return AbstractJsonList.this.get(AbstractJsonList.this.size()-1-index);}
    @Override
    public int size() {return AbstractJsonList.this.size();}
    @Override
    public IList<JsonNode> reversed() {return AbstractJsonList.this;}
  }
}
