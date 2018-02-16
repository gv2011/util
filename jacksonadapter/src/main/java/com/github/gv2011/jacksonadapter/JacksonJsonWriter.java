package com.github.gv2011.jacksonadapter;

/*-
 * #%L
 * jacksonadapter
 * %%
 * Copyright (C) 2018 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
import static com.github.gv2011.util.ex.Exceptions.call;

import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.github.gv2011.util.ex.ThrowingRunnable;
import com.github.gv2011.util.json.JsonWriter;

final class JacksonJsonWriter implements JsonWriter{

    private final JsonGenerator gen;

    JacksonJsonWriter(final JsonGenerator gen) {
        this.gen = gen;
    }

    @Override
    public void beginArray() {
        call((ThrowingRunnable)gen::writeStartArray);
    }

    @Override
    public void endArray() {
        call((ThrowingRunnable)gen::writeEndArray);
    }

    @Override
    public void nullValue() {
        call((ThrowingRunnable)gen::writeNull);
    }

    @Override
    public void writeString(final String value) {
        call(()->gen.writeString(value));
    }

    @Override
    public void writeBoolean(final boolean value) {
        call(()->gen.writeBoolean(value));
    }

    @Override
    public void writeNumber(final BigDecimal value) {
        call(()->gen.writeNumber(value));
    }

    @Override
    public void beginObject() {
        call((ThrowingRunnable)gen::writeStartObject);
    }

    @Override
    public void endObject() {
        call((ThrowingRunnable)gen::writeEndObject);
    }

    @Override
    public void name(final String key) {
        call(()->gen.writeFieldName(key));
    }

    @Override
    public void flush() {
        call((ThrowingRunnable)gen::flush);
    }

}
