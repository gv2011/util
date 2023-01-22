package com.github.gv2011.jacksonadapter;

import static com.fasterxml.jackson.core.JsonToken.END_ARRAY;
import static com.fasterxml.jackson.core.JsonToken.END_OBJECT;
import static com.fasterxml.jackson.core.JsonToken.FIELD_NAME;
import static com.fasterxml.jackson.core.JsonToken.START_ARRAY;
import static com.fasterxml.jackson.core.JsonToken.START_OBJECT;
import static com.fasterxml.jackson.core.JsonToken.VALUE_FALSE;
import static com.fasterxml.jackson.core.JsonToken.VALUE_NULL;
import static com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_FLOAT;
import static com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT;
import static com.fasterxml.jackson.core.JsonToken.VALUE_STRING;
import static com.fasterxml.jackson.core.JsonToken.VALUE_TRUE;
import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;

import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.json.Adapter;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonReader;
import com.github.gv2011.util.json.JsonWriter;

public class JacksonAdapter implements Adapter{

    private final JsonFactory jackson;

    public JacksonAdapter() {
        this(new JsonFactory());
    }

    public JacksonAdapter(final JsonFactory jackson) {
        this.jackson = jackson;
    }

    @Override
    public JsonWriter newJsonWriter(final Writer out, final boolean compact) {
        return new JacksonJsonWriter(call(()->jackson.createGenerator(out)));
    }

	@Override
	public JsonReader newJsonReader(final com.github.gv2011.util.json.JsonFactory jf, final Reader in) {
		throw new UnsupportedOperationException();
	}

    @Override
    public JsonNode deserialize(final com.github.gv2011.util.json.JsonFactory jsonFactory, final String json) {
        return deserialize(jsonFactory, call(()->{
            final JsonParser parser = jackson.createParser(json);
            parser.nextToken();
            return parser;
        }));
    }

    static JsonNode deserialize(final com.github.gv2011.util.json.JsonFactory jf, final JsonParser parser) {
      return call(()->{
        final JsonToken token = notNull(parser.currentToken());
        final JsonNode result;
        if(token.equals(VALUE_STRING)) result = jf.primitive(parser.getText());
        else if(token.equals(VALUE_NUMBER_FLOAT)) result = jf.primitive(parser.getDecimalValue());
        else if(token.equals(VALUE_NUMBER_INT)) result = jf.primitive(parser.getDecimalValue());
        else if(token.equals(VALUE_NULL)) result = jf.jsonNull();
        else if(token.equals(VALUE_FALSE)) result = jf.primitive(false);
        else if(token.equals(VALUE_TRUE)) result = jf.primitive(true);
        else if(token.equals(START_ARRAY)){
          notNull(parser.nextToken());
          result = XStream.fromIterator(new It(jf, parser)).collect(jf.toJsonList());
        }
        else if(token.equals(START_OBJECT)){
          notNull(parser.nextToken());
          result = XStream.fromIterator(new Itm(jf, parser)).collect(jf.toJsonObject());
        }
        else throw new IllegalArgumentException(token.toString());
        parser.nextToken();
        return result;
      });
    }

    private static class It implements Iterator<JsonNode> {

        private final JsonParser in;
        private final com.github.gv2011.util.json.JsonFactory jf;

        private It(final com.github.gv2011.util.json.JsonFactory jf, final JsonParser in) {
            this.jf = jf;
            this.in = in;
        }

        @Override
        public boolean hasNext() {
            return call(()->!in.currentToken().equals(END_ARRAY));
        }

        @Override
        public JsonNode next() {
            return deserialize(jf, in);
        }

    }

    private static class Itm implements Iterator<Pair<String,JsonNode>> {

        private final JsonParser in;
        private final com.github.gv2011.util.json.JsonFactory jf;

        private Itm(final com.github.gv2011.util.json.JsonFactory jf, final JsonParser in) {
            this.jf = jf;
            this.in = in;
        }

        @Override
        public boolean hasNext() {
            return call(()->!in.currentToken().equals(END_OBJECT));
        }

        @Override
        public Pair<String,JsonNode> next() {
            return call(()->{
                verify(in.currentToken().equals(FIELD_NAME));
                final String key = in.getText();
                notNull(in.nextToken());
                final JsonNode value = deserialize(jf, in);
                return pair(key, value);
            });
        }

    }

}
