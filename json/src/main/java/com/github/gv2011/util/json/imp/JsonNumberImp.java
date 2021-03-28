package com.github.gv2011.util.json.imp;

import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;
import com.github.gv2011.util.json.JsonNumber;
import com.github.gv2011.util.json.JsonWriter;
import com.github.gv2011.util.num.Decimal;

public class JsonNumberImp extends JsonPrimitiveImp<Decimal> implements JsonNumber{

  JsonNumberImp(final JsonFactoryImp f, final Decimal value) {
    super(f, value);
  }

  @Override
  public JsonNodeType jsonNodeType() {
    return JsonNodeType.NUMBER;
  }

  @Override
  public int compareWithOtherOfSameJsonNodeType(final JsonNode o) {
    return value.compareWithOtherOfSameType(o.asNumber());
  }

  @Override
  public void write(final JsonWriter out){
    out.writeDecimal(value);
  }

}
