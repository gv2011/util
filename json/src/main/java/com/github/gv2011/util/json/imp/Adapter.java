package com.github.gv2011.util.json.imp;

import java.io.Writer;

import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonWriter;
import com.github.gv2011.util.serviceloader.Service;

@Service(defaultImplementation="com.github.gv2011.jsong/com.github.gv2011.jsong.JsongAdapter")
public interface Adapter {

    JsonWriter newJsonWriter(Writer out);

    JsonNode deserialize(JsonFactoryImp jsonFactoryImp, String json);

}
