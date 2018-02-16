package com.github.gv2011.jsong;

/*-
 * #%L
 * jsong
 * %%
 * Copyright (C) 2017 - 2018 Vinz (https://github.com/gv2011)
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

import com.github.gv2011.util.json.JsonWriter;

final class JsongWriter implements JsonWriter {

    private final com.google.gson.stream.JsonWriter delegate;

    JsongWriter(final com.google.gson.stream.JsonWriter delegate) {
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
    public void writeNumber(final BigDecimal value) {
        call(()->delegate.value(value));
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
