package com.github.gv2011.jsong;

import static com.github.gv2011.util.ex.Exceptions.call;

import com.github.gv2011.util.json.JsonWriter;
import com.github.gv2011.util.num.Decimal;

final class JsongWriter implements JsonWriter {

    private final com.github.gv2011.gson.stream.JsonWriter delegate;

    JsongWriter(final com.github.gv2011.gson.stream.JsonWriter delegate) {
      this.delegate = delegate;
    }

    @Override
    public void beginArray() {
      call(delegate::beginArray);
    }

    @Override
    public void endArray() {
        call(delegate::endArray);
    }

    @Override
    public void nullValue() {
        call(delegate::nullValue);
    }

    @Override
    public void writeString(final String value) {
        call(()->delegate.value(value));
    }

    @Override
    public void writeBoolean(final boolean value) {
        call(()->delegate.value(value));
    }

    @Override
    public void writeDecimal(Decimal value) {
      call(()->delegate.value(value.toBigDecimal()));
    }

    @Override
    public void beginObject() {
        call(delegate::beginObject);
    }

    @Override
    public void endObject() {
        call(delegate::endObject);
    }

    @Override
    public void name(final String key) {
        call(()->delegate.name(key));
    }

    @Override
    public void flush() {
        call(delegate::flush);
    }


}
