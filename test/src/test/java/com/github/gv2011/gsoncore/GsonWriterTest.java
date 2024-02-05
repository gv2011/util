/*
 * Copyright (C) 2010 Google Inc.
 * Copyright (C) 2016-2021 Vinz (https://github.com/gv2011)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.gv2011.gsoncore;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.github.gv2011.util.num.NumUtils;

import junit.framework.TestCase;

@SuppressWarnings("resource")
public final class GsonWriterTest extends TestCase {

  public void testTopLevelValueTypes() {
    final StringWriter string1 = new StringWriter();
    final GsonWriter writer1 = new GsonWriter(string1);
    writer1.writeBoolean(true);
    writer1.close();
    assertEquals("true", string1.toString());

    final StringWriter string2 = new StringWriter();
    final GsonWriter writer2 = new GsonWriter(string2);
    writer2.nullValue();
    writer2.close();
    assertEquals("null", string2.toString());

    final StringWriter string3 = new StringWriter();
    final GsonWriter writer3 = new GsonWriter(string3);
    writer3.writeDecimal(NumUtils.num(123));
    writer3.close();
    assertEquals("123", string3.toString());

    final StringWriter string4 = new StringWriter();
    final GsonWriter writer4 = new GsonWriter(string4);
    writer4.writeDecimal(NumUtils.num(123.4));
    writer4.close();
    assertEquals("123.4", string4.toString());

    final StringWriter string5 = new StringWriter();
    final GsonWriter writert = new GsonWriter(string5);
    writert.writeString("a");
    writert.close();
    assertEquals("\"a\"", string5.toString());
  }

  public void testInvalidTopLevelTypes() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.name("hello");
    try {
      jsonWriter.writeString("world");
      fail();
    } catch (final IllegalStateException expected) {
    }
  }

  public void testTwoNames() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginObject();
    jsonWriter.name("a");
    try {
      jsonWriter.name("a");
      fail();
    } catch (final IllegalStateException expected) {
    }
  }

  public void testNameWithoutValue() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginObject();
    jsonWriter.name("a");
    try {
      jsonWriter.endObject();
      fail();
    } catch (final IllegalStateException expected) {
    }
  }

  public void testValueWithoutName() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginObject();
    try {
      jsonWriter.writeBoolean(true);
      fail();
    } catch (final IllegalStateException expected) {
    }
  }

  public void testMultipleTopLevelValues() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginArray();
    jsonWriter.endArray();
    try {
      jsonWriter.beginArray();
      fail();
    } catch (final IllegalStateException expected) {
    }
  }

  public void testBadNestingObject() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginArray();
    jsonWriter.beginObject();
    try {
      jsonWriter.endArray();
      fail();
    } catch (final IllegalStateException expected) {
    }
  }

  public void testBadNestingArray() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginArray();
    jsonWriter.beginArray();
    try {
      jsonWriter.endObject();
      fail();
    } catch (final IllegalStateException expected) {
    }
  }

  public void testNullName() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginObject();
    try {
      jsonWriter.name(null);
      fail();
    } catch (final NullPointerException expected) {
    }
  }

  public void testNullStringValue() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginArray();
    jsonWriter.nullValue();
    jsonWriter.endArray();
    assertThat(stringWriter.toString(), is("[\n  null\n]"));
  }

  public void testJsonValue() throws IOException {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginObject();
    jsonWriter.name("a");
    jsonWriter.jsonValue("{\"b\":true}");
    jsonWriter.name("c");
    jsonWriter.writeInt(1);
    jsonWriter.endObject();
    assertThat(
      stringWriter.toString(),
      is( "{\n"
        + "  \"a\": {\"b\":true},\n"
        + "  \"c\": 1\n"
        + "}"
      )
    );
  }

  public void testDoubles() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginArray();
    jsonWriter.writeDecimal(NumUtils.num(-0.0));
    jsonWriter.writeDecimal(NumUtils.num(1.0));
    jsonWriter.writeDecimal(NumUtils.num(Double.MAX_VALUE));
    jsonWriter.writeDecimal(NumUtils.num(Double.MIN_VALUE));
    jsonWriter.writeDecimal(NumUtils.num(0.0));
    jsonWriter.writeDecimal(NumUtils.num(-0.5));
    jsonWriter.writeDecimal(NumUtils.num(2.2250738585072014E-308));
    jsonWriter.writeDecimal(NumUtils.num(Math.PI));
    jsonWriter.writeDecimal(NumUtils.num(Math.E));
    jsonWriter.endArray();
    jsonWriter.close();
    assertThat(
      stringWriter.toString(),
      is( "[\n"
        + "  0,\n"
        + "  1,\n"
        + "  1.7976931348623157e+308,\n"
        + "  4.9e-324,\n"
        + "  0,\n"
        + "  -0.5,\n"
        + "  2.2250738585072014e-308,\n"
        + "  3.141592653589793,\n"
        + "  2.718281828459045\n"
        + "]"
      )
    );
  }

  public void testLongs() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginArray();
    jsonWriter.writeInt(0);
    jsonWriter.writeInt(1);
    jsonWriter.writeInt(-1);
    jsonWriter.writeLong(Long.MIN_VALUE);
    jsonWriter.writeLong(Long.MAX_VALUE);
    jsonWriter.endArray();
    jsonWriter.close();
    assertThat(
      stringWriter.toString(),
      is("[\n  0,\n  1,\n  -1,\n  -9223372036854775808,\n  9223372036854775807\n]"
      )
    );
  }

  public void testLongMAX_VALUE() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginArray();
    jsonWriter.writeLong(Long.MAX_VALUE);
    jsonWriter.endArray();
    jsonWriter.close();
    assertThat(
      stringWriter.toString(),
      is("[\n  9223372036854775807\n]"
      )
    );
  }

  public void testNumbers() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginArray();
    jsonWriter.writeDecimal(NumUtils.num(new BigInteger("0")));
    jsonWriter.writeDecimal(NumUtils.num(new BigInteger("9223372036854775808")));
    jsonWriter.writeDecimal(NumUtils.num(new BigInteger("-9223372036854775809")));
    jsonWriter.writeDecimal(NumUtils.num(new BigDecimal("3.141592653589793238462643383")));
    jsonWriter.endArray();
    jsonWriter.close();
    assertThat(
      stringWriter.toString(),
      is( "[\n"
        + "  0,\n"
        + "  9223372036854775808,\n"
        + "  -9223372036854775809,\n"
        + "  3.141592653589793238462643383\n"
        + "]"
      )
    );
  }

  public void testBooleans() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginArray();
    jsonWriter.writeBoolean(true);
    jsonWriter.writeBoolean(false);
    jsonWriter.endArray();
    assertThat(stringWriter.toString(), is("[\n  true,\n  false\n]"));
  }

  public void testNulls() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginArray();
    jsonWriter.nullValue();
    jsonWriter.endArray();
    assertEquals("[\n  null\n]", stringWriter.toString());
  }

  public void testStrings() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginArray();
    jsonWriter.writeString("a");
    jsonWriter.writeString("a\"");
    jsonWriter.writeString("\"");
    jsonWriter.writeString(":");
    jsonWriter.writeString(",");
    jsonWriter.writeString("\b");
    jsonWriter.writeString("\f");
    jsonWriter.writeString("\n");
    jsonWriter.writeString("\r");
    jsonWriter.writeString("\t");
    jsonWriter.writeString(" ");
    jsonWriter.writeString("\\");
    jsonWriter.writeString("{");
    jsonWriter.writeString("}");
    jsonWriter.writeString("[");
    jsonWriter.writeString("]");
    jsonWriter.writeString("\0");
    jsonWriter.writeString("\u0019");
    jsonWriter.endArray();
    assertThat(
      stringWriter.toString(),
      is( "[\n"
        + "  \"a\",\n"
        + "  \"a\\\"\",\n"
        + "  \"\\\"\",\n"
        + "  \":\",\n"
        + "  \",\",\n"
        + "  \"\\b\",\n"
        + "  \"\\f\",\n"
        + "  \"\\n\",\n"
        + "  \"\\r\",\n"
        + "  \"\\t\",\n"
        + "  \" \",\n"
        + "  \"\\\\\",\n"
        + "  \"{\",\n"
        + "  \"}\",\n"
        + "  \"[\",\n"
        + "  \"]\",\n"
        + "  \"\\u0000\",\n"
        + "  \"\\u0019\"\n"
        + "]"
      )
    );
  }

  public void testUnicodeLineBreaksEscaped() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginArray();
    jsonWriter.writeString("\u2028 \u2029");
    jsonWriter.endArray();
    assertThat(
      stringWriter.toString(),
      is("[\n  \"\\u2028 \\u2029\"\n]")
    );
  }

  public void testEmptyArray() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginArray();
    jsonWriter.endArray();
    assertEquals("[]", stringWriter.toString());
  }

  public void testEmptyObject() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginObject();
    jsonWriter.endObject();
    assertEquals("{}", stringWriter.toString());
  }

  public void testObjectsInArrays() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginArray();
    jsonWriter.beginObject();
    jsonWriter.name("a");
    jsonWriter.writeInt(5);
    jsonWriter.name("b");
    jsonWriter.writeBoolean(false);
    jsonWriter.endObject();
    jsonWriter.beginObject();
    jsonWriter.name("c");
    jsonWriter.writeInt(6);
    jsonWriter.name("d");
    jsonWriter.writeBoolean(true);
    jsonWriter.endObject();
    jsonWriter.endArray();
    assertThat(
      stringWriter.toString(),
      is( "[\n"
        + "  {\n"
        + "    \"a\": 5,\n"
        + "    \"b\": false\n"
        + "  },\n"
        + "  {\n"
        + "    \"c\": 6,\n"
        + "    \"d\": true\n"
        + "  }\n"
        + "]"
      )
    );
  }

  public void testArraysInObjects() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginObject();
    jsonWriter.name("a");
    jsonWriter.beginArray();
    jsonWriter.writeInt(5);
    jsonWriter.writeBoolean(false);
    jsonWriter.endArray();
    jsonWriter.name("b");
    jsonWriter.beginArray();
    jsonWriter.writeInt(6);
    jsonWriter.writeBoolean(true);
    jsonWriter.endArray();
    jsonWriter.endObject();
    assertThat(
      stringWriter.toString(),
      is(   "{\n"
          + "  \"a\": [\n"
          + "    5,\n"
          + "    false\n"
          + "  ],\n"
          + "  \"b\": [\n"
          + "    6,\n"
          + "    true\n"
          + "  ]\n"
          + "}"
        )
    );
  }

  public void testDeepNestingArrays() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    for (int i = 0; i < 20; i++) {
      jsonWriter.beginArray();
    }
    for (int i = 0; i < 20; i++) {
      jsonWriter.endArray();
    }
    assertThat(
      stringWriter.toString(),
      is(
        "[\n"
        + "  [\n"
        + "    [\n"
        + "      [\n"
        + "        [\n"
        + "          [\n"
        + "            [\n"
        + "              [\n"
        + "                [\n"
        + "                  [\n"
        + "                    [\n"
        + "                      [\n"
        + "                        [\n"
        + "                          [\n"
        + "                            [\n"
        + "                              [\n"
        + "                                [\n"
        + "                                  [\n"
        + "                                    [\n"
        + "                                      []\n"
        + "                                    ]\n"
        + "                                  ]\n"
        + "                                ]\n"
        + "                              ]\n"
        + "                            ]\n"
        + "                          ]\n"
        + "                        ]\n"
        + "                      ]\n"
        + "                    ]\n"
        + "                  ]\n"
        + "                ]\n"
        + "              ]\n"
        + "            ]\n"
        + "          ]\n"
        + "        ]\n"
        + "      ]\n"
        + "    ]\n"
        + "  ]\n"
        + "]"
      ));
  }

  public void testDeepNestingObjects() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginObject();
    for (int i = 0; i < 20; i++) {
      jsonWriter.name("a");
      jsonWriter.beginObject();
    }
    for (int i = 0; i < 20; i++) {
      jsonWriter.endObject();
    }
    jsonWriter.endObject();
    assertThat(
      stringWriter.toString(),
      is( "{\n"
        + "  \"a\": {\n"
        + "    \"a\": {\n"
        + "      \"a\": {\n"
        + "        \"a\": {\n"
        + "          \"a\": {\n"
        + "            \"a\": {\n"
        + "              \"a\": {\n"
        + "                \"a\": {\n"
        + "                  \"a\": {\n"
        + "                    \"a\": {\n"
        + "                      \"a\": {\n"
        + "                        \"a\": {\n"
        + "                          \"a\": {\n"
        + "                            \"a\": {\n"
        + "                              \"a\": {\n"
        + "                                \"a\": {\n"
        + "                                  \"a\": {\n"
        + "                                    \"a\": {\n"
        + "                                      \"a\": {\n"
        + "                                        \"a\": {}\n"
        + "                                      }\n"
        + "                                    }\n"
        + "                                  }\n"
        + "                                }\n"
        + "                              }\n"
        + "                            }\n"
        + "                          }\n"
        + "                        }\n"
        + "                      }\n"
        + "                    }\n"
        + "                  }\n"
        + "                }\n"
        + "              }\n"
        + "            }\n"
        + "          }\n"
        + "        }\n"
        + "      }\n"
        + "    }\n"
        + "  }\n"
        + "}"
      )
    );
  }

  public void testRepeatedName() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter);
    jsonWriter.beginObject();
    jsonWriter.name("a");
    jsonWriter.writeBoolean(true);
    jsonWriter.name("a");
    jsonWriter.writeBoolean(false);
    jsonWriter.endObject();
    // JsonWriter doesn't attempt to detect duplicate names
    assertThat(stringWriter.toString(), is("{\n  \"a\": true,\n  \"a\": false\n}"));
  }

  public void testPrettyPrintObject() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter, "   ");

    jsonWriter.beginObject();
    jsonWriter.name("a");
    jsonWriter.writeBoolean(true);
    jsonWriter.name("b");
    jsonWriter.writeBoolean(false);
    jsonWriter.name("c");
    jsonWriter.writeDecimal(NumUtils.num(5.0));
    jsonWriter.name("e");
    jsonWriter.nullValue();
    jsonWriter.name("f");
    jsonWriter.beginArray();
    jsonWriter.writeDecimal(NumUtils.num(6.0));
    jsonWriter.writeDecimal(NumUtils.num(7.1));
    jsonWriter.endArray();
    jsonWriter.name("g");
    jsonWriter.beginObject();
    jsonWriter.name("h");
    jsonWriter.writeDecimal(NumUtils.num(8.0));
    jsonWriter.name("i");
    jsonWriter.writeDecimal(NumUtils.num(9.0));
    jsonWriter.endObject();
    jsonWriter.endObject();

    final String expected =
        "{\n"
      + "   \"a\": true,\n"
      + "   \"b\": false,\n"
      + "   \"c\": 5,\n"
      + "   \"f\": [\n"
      + "      6,\n"
      + "      7.1\n"
      + "   ],\n"
      + "   \"g\": {\n"
      + "      \"h\": 8,\n"
      + "      \"i\": 9\n"
      + "   }\n"
      + "}"
    ;
    assertThat(stringWriter.toString(), is(expected));
  }

  public void testPrettyPrintArray() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter jsonWriter = new GsonWriter(stringWriter, "   ");

    jsonWriter.beginArray();
    jsonWriter.writeBoolean(true);
    jsonWriter.writeBoolean(false);
    jsonWriter.writeDecimal(NumUtils.num(5.0));
    jsonWriter.nullValue();
    jsonWriter.beginObject();
    jsonWriter.name("a");
    jsonWriter.writeDecimal(NumUtils.num(6.0));
    jsonWriter.name("b");
    jsonWriter.writeDecimal(NumUtils.num(7.0));
    jsonWriter.endObject();
    jsonWriter.beginArray();
    jsonWriter.writeDecimal(NumUtils.num(8.0));
    jsonWriter.writeDecimal(NumUtils.num(9.0));
    jsonWriter.endArray();
    jsonWriter.endArray();

    final String expected = "[\n"
        + "   true,\n"
        + "   false,\n"
        + "   5,\n"
        + "   null,\n"
        + "   {\n"
        + "      \"a\": 6,\n"
        + "      \"b\": 7\n"
        + "   },\n"
        + "   [\n"
        + "      8,\n"
        + "      9\n"
        + "   ]\n"
        + "]";
    assertEquals(expected, stringWriter.toString());
  }

  public void testLenientWriterPermitsMultipleTopLevelValues() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter writer = new GsonWriter(stringWriter, GsonWriter.DEFAULT_INDENT, true, false, false);
    writer.beginArray();
    writer.endArray();
    writer.beginArray();
    writer.endArray();
    writer.close();
    assertEquals("[][]", stringWriter.toString());
  }

  public void testStrictWriterDoesNotPermitMultipleTopLevelValues() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter writer = new GsonWriter(stringWriter);
    writer.beginArray();
    writer.endArray();
    try {
      writer.beginArray();
      fail();
    } catch (final IllegalStateException expected) {
    }
  }

  public void testClosedWriterThrowsOnStructure() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter writer = new GsonWriter(stringWriter);
    writer.beginArray();
    writer.endArray();
    writer.close();
    try {
      writer.beginArray();
      fail();
    } catch (final IllegalStateException expected) {
    }
    try {
      writer.endArray();
      fail();
    } catch (final IllegalStateException expected) {
    }
    try {
      writer.beginObject();
      fail();
    } catch (final IllegalStateException expected) {
    }
    try {
      writer.endObject();
      fail();
    } catch (final IllegalStateException expected) {
    }
  }

  public void testClosedWriterThrowsOnName() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter writer = new GsonWriter(stringWriter);
    writer.beginArray();
    writer.endArray();
    writer.close();
    try {
      writer.name("a");
      fail();
    } catch (final IllegalStateException expected) {
    }
  }

  public void testClosedWriterThrowsOnValue() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter writer = new GsonWriter(stringWriter);
    writer.beginArray();
    writer.endArray();
    writer.close();
    try {
      writer.writeString("a");
      fail();
    } catch (final IllegalStateException expected) {
    }
  }

  public void testClosedWriterThrowsOnFlush() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter writer = new GsonWriter(stringWriter);
    writer.beginArray();
    writer.endArray();
    writer.close();
    try {
      writer.flush();
      fail();
    } catch (final IllegalStateException expected) {
    }
  }

  public void testWriterCloseIsIdempotent() {
    final StringWriter stringWriter = new StringWriter();
    final GsonWriter writer = new GsonWriter(stringWriter);
    writer.beginArray();
    writer.endArray();
    writer.close();
    writer.close();
  }
}
