package com.github.gv2011.util.json;

import java.math.BigDecimal;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collector;

import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.serviceloader.Service;

@Service(defaultImplementation="com.github.gv2011.util.json.imp/com.github.gv2011.util.json.imp.JsonFactoryImp")
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
