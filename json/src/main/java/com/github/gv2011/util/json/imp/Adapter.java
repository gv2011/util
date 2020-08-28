package com.github.gv2011.util.json.imp;

import java.io.Reader;
import java.io.Writer;

import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonReader;
import com.github.gv2011.util.json.JsonWriter;
import com.github.gv2011.util.serviceloader.Service;

@Service(defaultImplementation="com.github.gv2011.jsong/com.github.gv2011.jsong.JsongAdapter")
public interface Adapter {

    JsonNode deserialize(JsonFactory jsonFactory, String json);

	JsonReader newJsonReader(final JsonFactory jf, Reader in);

	JsonWriter newJsonWriter(Writer out);

}
