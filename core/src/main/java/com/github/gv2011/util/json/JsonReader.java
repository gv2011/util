package com.github.gv2011.util.json;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.icol.Opt;


public interface JsonReader extends AutoCloseableNt {

  void readArrayStart();

  void readArrayEnd();

  void readObjectStart();

  void readObjectEnd();

  boolean hasNext();

  Opt<JsonNodeType> nextType();
  
  JsonNode readNode();

  JsonList readList();

  JsonObject readObject();

  String readName();

  JsonPrimitive<?> readPrimitive();
  
  <P> JsonPrimitive<P> readPrimitive(Class<P> clazz);

}
