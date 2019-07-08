package com.github.gv2011.util.json;

import java.math.BigDecimal;

public interface JsonWriter {

    void beginArray();

    void endArray();

    void nullValue();

    void writeString(String value);

    void writeBoolean(boolean value);

    void writeNumber(BigDecimal value);

    default void writeInt(final int value) {
        writeNumber(BigDecimal.valueOf(value));
    }

    default void writeLong(final long value) {
        writeNumber(BigDecimal.valueOf(value));
    }

    void beginObject();

    void endObject();

    void name(String key);

    void flush();

}
