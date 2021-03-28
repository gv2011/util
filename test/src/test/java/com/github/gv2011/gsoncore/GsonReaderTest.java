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

import static com.github.gv2011.gsoncore.JsonToken.BEGIN_ARRAY;
import static com.github.gv2011.gsoncore.JsonToken.BEGIN_OBJECT;
import static com.github.gv2011.gsoncore.JsonToken.BOOLEAN;
import static com.github.gv2011.gsoncore.JsonToken.END_ARRAY;
import static com.github.gv2011.gsoncore.JsonToken.END_OBJECT;
import static com.github.gv2011.gsoncore.JsonToken.NAME;
import static com.github.gv2011.gsoncore.JsonToken.NULL;
import static com.github.gv2011.gsoncore.JsonToken.NUMBER;
import static com.github.gv2011.gsoncore.JsonToken.STRING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

import com.github.gv2011.util.num.NumUtils;

import junit.framework.TestCase;

public final class GsonReaderTest extends TestCase {
  public void testReadArray() {
    GsonReader reader = new GsonReader(reader("[true, true]"));
    reader.readArrayStart();
    assertEquals(true, reader.readBooleanRaw());
    assertEquals(true, reader.readBooleanRaw());
    reader.readArrayEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testReadEmptyArray() {
    GsonReader reader = new GsonReader(reader("[]"));
    reader.readArrayStart();
    assertFalse(reader.hasNext());
    reader.readArrayEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testReadObject() {
    GsonReader reader = new GsonReader(reader(
        "{\"a\": \"android\", \"b\": \"banana\"}"));
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    assertEquals("android", reader.readStringRaw());
    assertEquals("b", reader.readName());
    assertEquals("banana", reader.readStringRaw());
    reader.readObjectEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testReadEmptyObject() {
    GsonReader reader = new GsonReader(reader("{}"));
    reader.readObjectStart();
    assertFalse(reader.hasNext());
    reader.readObjectEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testSkipArray() {
    GsonReader reader = new GsonReader(reader(
        "{\"a\": [\"one\", \"two\", \"three\"], \"b\": 123}"));
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    reader.skipValue();
    assertEquals("b", reader.readName());
    assertEquals(123, reader.nextInt());
    reader.readObjectEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testSkipArrayAfterPeek() throws Exception {
    GsonReader reader = new GsonReader(reader(
        "{\"a\": [\"one\", \"two\", \"three\"], \"b\": 123}"));
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    assertEquals(BEGIN_ARRAY, reader.peek());
    reader.skipValue();
    assertEquals("b", reader.readName());
    assertEquals(123, reader.nextInt());
    reader.readObjectEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testSkipTopLevelObject() throws Exception {
    GsonReader reader = new GsonReader(reader(
        "{\"a\": [\"one\", \"two\", \"three\"], \"b\": 123}"));
    reader.skipValue();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testSkipObject() {
    GsonReader reader = new GsonReader(reader(
        "{\"a\": { \"c\": [], \"d\": [true, true, {}] }, \"b\": \"banana\"}"));
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    reader.skipValue();
    assertEquals("b", reader.readName());
    reader.skipValue();
    reader.readObjectEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testSkipObjectAfterPeek() throws Exception {
    String json = "{" + "  \"one\": { \"num\": 1 }"
        + ", \"two\": { \"num\": 2 }" + ", \"three\": { \"num\": 3 }" + "}";
    GsonReader reader = new GsonReader(reader(json));
    reader.readObjectStart();
    assertEquals("one", reader.readName());
    assertEquals(BEGIN_OBJECT, reader.peek());
    reader.skipValue();
    assertEquals("two", reader.readName());
    assertEquals(BEGIN_OBJECT, reader.peek());
    reader.skipValue();
    assertEquals("three", reader.readName());
    reader.skipValue();
    reader.readObjectEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testSkipInteger() {
    GsonReader reader = new GsonReader(reader(
        "{\"a\":123456789,\"b\":-123456789}"));
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    reader.skipValue();
    assertEquals("b", reader.readName());
    reader.skipValue();
    reader.readObjectEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testSkipDouble() {
    GsonReader reader = new GsonReader(reader(
        "{\"a\":-123.456e-789,\"b\":123456789.0}"));
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    reader.skipValue();
    assertEquals("b", reader.readName());
    reader.skipValue();
    reader.readObjectEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testHelloWorld() {
    String json = "{\n" +
        "   \"hello\": true,\n" +
        "   \"foo\": [\"world\"]\n" +
        "}";
    GsonReader reader = new GsonReader(reader(json));
    reader.readObjectStart();
    assertEquals("hello", reader.readName());
    assertEquals(true, reader.readBooleanRaw());
    assertEquals("foo", reader.readName());
    reader.readArrayStart();
    assertEquals("world", reader.readStringRaw());
    reader.readArrayEnd();
    reader.readObjectEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testEmptyString() {
    try {
      new GsonReader(reader("")).readArrayStart();
      fail();
    } catch (MalformedJsonException expected) {
    }
    try {
      new GsonReader(reader("")).readObjectStart();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testCharacterUnescaping() {
    String json = "[\"a\","
        + "\"a\\\"\","
        + "\"\\\"\","
        + "\":\","
        + "\",\","
        + "\"\\b\","
        + "\"\\f\","
        + "\"\\n\","
        + "\"\\r\","
        + "\"\\t\","
        + "\" \","
        + "\"\\\\\","
        + "\"{\","
        + "\"}\","
        + "\"[\","
        + "\"]\","
        + "\"\\u0000\","
        + "\"\\u0019\","
        + "\"\\u20AC\""
        + "]";
    GsonReader reader = new GsonReader(reader(json));
    reader.readArrayStart();
    assertEquals("a", reader.readStringRaw());
    assertEquals("a\"", reader.readStringRaw());
    assertEquals("\"", reader.readStringRaw());
    assertEquals(":", reader.readStringRaw());
    assertEquals(",", reader.readStringRaw());
    assertEquals("\b", reader.readStringRaw());
    assertEquals("\f", reader.readStringRaw());
    assertEquals("\n", reader.readStringRaw());
    assertEquals("\r", reader.readStringRaw());
    assertEquals("\t", reader.readStringRaw());
    assertEquals(" ", reader.readStringRaw());
    assertEquals("\\", reader.readStringRaw());
    assertEquals("{", reader.readStringRaw());
    assertEquals("}", reader.readStringRaw());
    assertEquals("[", reader.readStringRaw());
    assertEquals("]", reader.readStringRaw());
    assertEquals("\0", reader.readStringRaw());
    assertEquals("\u0019", reader.readStringRaw());
    assertEquals("\u20AC", reader.readStringRaw());
    reader.readArrayEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testUnescapingInvalidCharacters() {
    String json = "[\"\\u000g\"]";
    GsonReader reader = new GsonReader(reader(json));
    reader.readArrayStart();
    try {
      reader.readStringRaw();
      fail();
    } catch (NumberFormatException expected) {
    }
  }

  public void testUnescapingTruncatedCharacters() {
    String json = "[\"\\u000";
    GsonReader reader = new GsonReader(reader(json));
    reader.readArrayStart();
    try {
      reader.readStringRaw();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testUnescapingTruncatedSequence() {
    String json = "[\"\\";
    GsonReader reader = new GsonReader(reader(json));
    reader.readArrayStart();
    try {
      reader.readStringRaw();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testIntegersWithFractionalPartSpecified() {
    GsonReader reader = new GsonReader(reader("[1.0,1.0,1.0]"));
    reader.readArrayStart();
    assertEquals(1.0, reader.nextDouble());
    assertEquals(1, reader.nextInt());
    assertEquals(1L, reader.nextLong());
  }

  public void testDoubles() {
    String json = "[-0.0,"
        + "1.0,"
        + "1.7976931348623157E308,"
        + "4.9E-324,"
        + "0.0,"
        + "-0.5,"
        + "2.2250738585072014E-308,"
        + "3.141592653589793,"
        + "2.718281828459045]";
    GsonReader reader = new GsonReader(reader(json));
    reader.readArrayStart();
    assertEquals(0.0, reader.nextDouble());
    assertEquals(1.0, reader.nextDouble());
    assertEquals(1.7976931348623157E308, reader.nextDouble());
    assertEquals(4.9E-324, reader.nextDouble());
    assertEquals(0.0, reader.nextDouble());
    assertEquals(-0.5, reader.nextDouble());
    assertEquals(2.2250738585072014E-308, reader.nextDouble());
    assertEquals(3.141592653589793, reader.nextDouble());
    assertEquals(2.718281828459045, reader.nextDouble());
    reader.readArrayEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testStrictNonFiniteDoubles() {
    String json = "[NaN]";
    GsonReader reader = new GsonReader(reader(json));
    reader.readArrayStart();
    try {
      reader.nextDouble();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testStrictQuotedNonFiniteDoubles() {
    String json = "[\"NaN\"]";
    GsonReader reader = new GsonReader(reader(json));
    reader.readArrayStart();
    try {
      reader.nextDouble();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testLenientNonFiniteDoubles() {
    String json = "[NaN, -Infinity, Infinity]";
    GsonReader reader = new GsonReader(reader(json), true);
    reader.readArrayStart();
    assertTrue(Double.isNaN(reader.nextDoubleOld()));
    assertEquals(Double.NEGATIVE_INFINITY, reader.nextDoubleOld());
    assertEquals(Double.POSITIVE_INFINITY, reader.nextDoubleOld());
    reader.readArrayEnd();
  }

  public void testLenientQuotedNonFiniteDoubles() {
    String json = "[\"NaN\", \"-Infinity\", \"Infinity\"]";
    GsonReader reader = new GsonReader(reader(json), true);
    reader.readArrayStart();
    try {
      reader.nextDouble();
      fail();
    } catch (MalformedJsonException e) {
      assertThat(reader.readStringRaw(), is("NaN"));
    }
    try {
      reader.nextDouble();
      fail();
    } catch (MalformedJsonException e) {
      assertThat(reader.readStringRaw(), is("-Infinity"));
    }
    try {
      reader.nextDouble();
      fail();
    } catch (MalformedJsonException e) {
      assertThat(reader.readStringRaw(), is("Infinity"));
    }
    reader.readArrayEnd();
  }

  public void testStrictNonFiniteDoublesWithSkipValue() {
    String json = "[NaN]";
    GsonReader reader = new GsonReader(reader(json));
    reader.readArrayStart();
    try {
      reader.skipValue();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testLongs() {
    String json = "[0,0,0,"
        + "1,1,1,"
        + "-1,-1,-1,"
        + "-9223372036854775808,"
        + "9223372036854775807]";
    GsonReader reader = new GsonReader(reader(json));
    reader.readArrayStart();
    assertEquals(0L, reader.nextLong());
    assertEquals(0, reader.nextInt());
    assertEquals(0.0, reader.nextDouble());
    assertEquals(1L, reader.nextLong());
    assertEquals(1, reader.nextInt());
    assertEquals(1.0, reader.nextDouble());
    assertEquals(-1L, reader.nextLong());
    assertEquals(-1, reader.nextInt());
    assertEquals(-1.0, reader.nextDouble());
    try {
      reader.nextInt();
      fail();
    } catch (NumberFormatException expected) {
    }
    assertEquals(Long.MIN_VALUE, reader.nextLong());
    try {
      reader.nextInt();
      fail();
    } catch (NumberFormatException expected) {
    }
    assertEquals(Long.MAX_VALUE, reader.nextLong());
    reader.readArrayEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testBigNumber() {
    String json = "[-1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678234567890e+99]";
    GsonReader reader = new GsonReader(reader(json));
    reader.readArrayStart();
    assertEquals(
      NumUtils.parse("-1.23456789012345678901234567890123456789012345678901234567890123456789012345678901234567823456789e+195"), 
      reader.readNumberRaw()
    );
    reader.readArrayEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void disabled_testNumberWithOctalPrefix() {
    String json = "[01]";
    GsonReader reader = new GsonReader(reader(json));
    reader.readArrayStart();
    try {
      reader.peek();
      fail();
    } catch (MalformedJsonException expected) {
    }
    try {
      reader.nextInt();
      fail();
    } catch (MalformedJsonException expected) {
    }
    try {
      reader.nextLong();
      fail();
    } catch (MalformedJsonException expected) {
    }
    try {
      reader.nextDouble();
      fail();
    } catch (MalformedJsonException expected) {
    }
    assertEquals("01", reader.readStringRaw());
    reader.readArrayEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testBooleans() {
    GsonReader reader = new GsonReader(reader("[true,false]"));
    reader.readArrayStart();
    assertEquals(true, reader.readBooleanRaw());
    assertEquals(false, reader.readBooleanRaw());
    reader.readArrayEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testPeekingUnquotedStringsPrefixedWithBooleans() {
    GsonReader reader = new GsonReader(reader("[truey]"), true);
    reader.readArrayStart();
    assertEquals(STRING, reader.peek());
    try {
      reader.readBooleanRaw();
      fail();
    } catch (IllegalStateException expected) {
    }
    assertEquals("truey", reader.readStringRaw());
    reader.readArrayEnd();
  }

  public void testMalformedNumbers() {
    assertNotANumber("-");
    assertNotANumber(".");

    // exponent lacks digit
    assertNotANumber("e");
    assertNotANumber("0e");
    assertNotANumber(".e");
    assertNotANumber("0.e");
    assertNotANumber("-.0e");

    // no integer
    assertNotANumber("e1");
    assertNotANumber(".e1");
    assertNotANumber("-e1");

    // trailing characters
    assertNotANumber("1x");
    assertNotANumber("1.1x");
    assertNotANumber("1e1x");
    assertNotANumber("1ex");
    assertNotANumber("1.1ex");
    assertNotANumber("1.1e1x");

    // fraction has no digit
    assertNotANumber("0.");
    assertNotANumber("-0.");
    assertNotANumber("0.e1");
    assertNotANumber("-0.e1");

    // no leading digit
    assertNotANumber(".0");
    assertNotANumber("-.0");
    assertNotANumber(".0e1");
    assertNotANumber("-.0e1");
  }

  private void assertNotANumber(String s) {
    GsonReader reader = new GsonReader(reader("[" + s + "]"), true);
    reader.readArrayStart();
    assertEquals(JsonToken.STRING, reader.peek());
    assertEquals(s, reader.readStringRaw());
    reader.readArrayEnd();
  }

  public void testPeekingUnquotedStringsPrefixedWithIntegers() {
    GsonReader reader = new GsonReader(reader("[12.34e5x]"), true);
   reader.readArrayStart();
    assertEquals(STRING, reader.peek());
    try {
      reader.nextInt();
      fail();
    } catch (IllegalStateException expected) {
    }
    assertEquals("12.34e5x", reader.readStringRaw());
  }

  public void testPeekLongMinValue() {
    GsonReader reader = new GsonReader(reader("[-9223372036854775808]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(NUMBER, reader.peek());
    assertEquals(-9223372036854775808L, reader.nextLong());
  }

  public void testPeekLongMaxValue() {
    GsonReader reader = new GsonReader(reader("[9223372036854775807]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(NUMBER, reader.peek());
    assertEquals(9223372036854775807L, reader.nextLong());
  }

  public void testLongLargerThanMaxLongThatWrapsAround() {
    GsonReader reader = new GsonReader(reader("[22233720368547758070]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(NUMBER, reader.peek());
    try {
      reader.nextLong();
      fail();
    } catch (NumberFormatException expected) {
    }
  }

  public void testLongLargerThanMinLongThatWrapsAround() {
    GsonReader reader = new GsonReader(reader("[-22233720368547758070]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(NUMBER, reader.peek());
    try {
      reader.nextLong();
      fail();
    } catch (NumberFormatException expected) {
    }
  }

  /**
   * This test fails because there's no double for 9223372036854775808, and our
   * long parsing uses Double.parseDouble() for fractional values.
   */
  public void disabled_testPeekLargerThanLongMaxValue() {
    GsonReader reader = new GsonReader(reader("[9223372036854775808]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(NUMBER, reader.peek());
    try {
      reader.nextLong();
      fail();
    } catch (NumberFormatException e) {
    }
  }

  /**
   * This test fails because there's no double for -9223372036854775809, and our
   * long parsing uses Double.parseDouble() for fractional values.
   */
  public void disabled_testPeekLargerThanLongMinValue() {
    GsonReader reader = new GsonReader(reader("[-9223372036854775809]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(NUMBER, reader.peek());
    try {
      reader.nextLong();
      fail();
    } catch (NumberFormatException expected) {
    }
    assertEquals(-9223372036854775809d, reader.nextDouble());
  }

  /**
   * This test fails because there's no double for 9223372036854775806, and
   * our long parsing uses Double.parseDouble() for fractional values.
   */
  public void disabled_testHighPrecisionLong() {
    String json = "[9223372036854775806.000]";
    GsonReader reader = new GsonReader(reader(json));
    reader.readArrayStart();
    assertEquals(9223372036854775806L, reader.nextLong());
    reader.readArrayEnd();
  }

  public void testPeekMuchLargerThanLongMinValue() {
    GsonReader reader = new GsonReader(reader("[-92233720368547758080]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(NUMBER, reader.peek());
    try {
      reader.nextLong();
      fail();
    } catch (NumberFormatException expected) {
    }
    assertEquals(-92233720368547758080d, reader.nextDouble());
  }

  public void testQuotedNumberWithEscape() {
    GsonReader reader = new GsonReader(reader("[\"12\u00334\"]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(STRING, reader.peek());
    assertEquals(1234, reader.nextInt());
  }

  public void testMixedCaseLiterals() {
    GsonReader reader = new GsonReader(reader("[True,TruE,False,FALSE,NULL,nulL]"));
    reader.readArrayStart();
    assertEquals(true, reader.readBooleanRaw());
    assertEquals(true, reader.readBooleanRaw());
    assertEquals(false, reader.readBooleanRaw());
    assertEquals(false, reader.readBooleanRaw());
    reader.readNullRaw();
    reader.readNullRaw();
    reader.readArrayEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testMissingValue() {
    GsonReader reader = new GsonReader(reader("{\"a\":}"));
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    try {
      reader.readStringRaw();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testPrematureEndOfInput() {
    GsonReader reader = new GsonReader(reader("{\"a\":true,"));
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    assertEquals(true, reader.readBooleanRaw());
    try {
      reader.readName();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testPrematurelyClosed() {
    try {
      GsonReader reader = new GsonReader(reader("{\"a\":[]}"));
      reader.readObjectStart();
      reader.close();
      reader.readName();
      fail();
    } catch (IllegalStateException expected) {
    }

    try {
      GsonReader reader = new GsonReader(reader("{\"a\":[]}"));
      reader.close();
      reader.readObjectStart();
      fail();
    } catch (IllegalStateException expected) {
    }

    try {
      GsonReader reader = new GsonReader(reader("{\"a\":true}"));
      reader.readObjectStart();
      reader.readName();
      reader.peek();
      reader.close();
      reader.readBooleanRaw();
      fail();
    } catch (IllegalStateException expected) {
    }
  }

  public void testNextFailuresDoNotAdvance() {
    GsonReader reader = new GsonReader(reader("{\"a\":true}"));
    reader.readObjectStart();
    try {
      reader.readStringRaw();
      fail();
    } catch (IllegalStateException expected) {
    }
    assertEquals("a", reader.readName());
    try {
      reader.readName();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.readArrayStart();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.readArrayEnd();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.readObjectStart();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.readObjectEnd();
      fail();
    } catch (IllegalStateException expected) {
    }
    assertEquals(true, reader.readBooleanRaw());
    try {
      reader.readStringRaw();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.readName();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.readArrayStart();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.readArrayEnd();
      fail();
    } catch (IllegalStateException expected) {
    }
    reader.readObjectEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    reader.close();
  }

  public void testIntegerMismatchFailuresDoNotAdvance() {
    GsonReader reader = new GsonReader(reader("[1.5]"));
    reader.readArrayStart();
    try {
      reader.nextInt();
      fail();
    } catch (NumberFormatException expected) {
    }
    assertEquals(1.5d, reader.nextDouble());
    reader.readArrayEnd();
  }

  public void testStringNullIsNotNull() {
    GsonReader reader = new GsonReader(reader("[\"null\"]"));
    reader.readArrayStart();
    try {
      reader.readNullRaw();
      fail();
    } catch (IllegalStateException expected) {
    }
  }

  public void testNullLiteralIsNotAString() {
    GsonReader reader = new GsonReader(reader("[null]"));
    reader.readArrayStart();
    try {
      reader.readStringRaw();
      fail();
    } catch (IllegalStateException expected) {
    }
  }

  public void testStrictNameValueSeparator() {
    GsonReader reader = new GsonReader(reader("{\"a\"=true}"));
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    try {
      reader.readBooleanRaw();
      fail();
    } catch (MalformedJsonException expected) {
    }

    reader = new GsonReader(reader("{\"a\"=>true}"));
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    try {
      reader.readBooleanRaw();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testLenientNameValueSeparator() {
    GsonReader reader = new GsonReader(reader("{\"a\"=true}"), true);
    //reader.setLenient(true);
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    assertEquals(true, reader.readBooleanRaw());

    reader = new GsonReader(reader("{\"a\"=>true}"), true);
    //reader.setLenient(true);
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    assertEquals(true, reader.readBooleanRaw());
  }

  public void testStrictNameValueSeparatorWithSkipValue() {
    GsonReader reader = new GsonReader(reader("{\"a\"=true}"));
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    try {
      reader.skipValue();
      fail();
    } catch (MalformedJsonException expected) {
    }

    reader = new GsonReader(reader("{\"a\"=>true}"));
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    try {
      reader.skipValue();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testCommentsInStringValue() throws Exception {
    GsonReader reader = new GsonReader(reader("[\"// comment\"]"));
    reader.readArrayStart();
    assertEquals("// comment", reader.readStringRaw());
    reader.readArrayEnd();

    reader = new GsonReader(reader("{\"a\":\"#someComment\"}"));
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    assertEquals("#someComment", reader.readStringRaw());
    reader.readObjectEnd();

    reader = new GsonReader(reader("{\"#//a\":\"#some //Comment\"}"));
    reader.readObjectStart();
    assertEquals("#//a", reader.readName());
    assertEquals("#some //Comment", reader.readStringRaw());
    reader.readObjectEnd();
  }

  public void testStrictComments() {
    GsonReader reader = new GsonReader(reader("[// comment \n true]"));
    reader.readArrayStart();
    try {
      reader.readBooleanRaw();
      fail();
    } catch (MalformedJsonException expected) {
    }

    reader = new GsonReader(reader("[# comment \n true]"));
    reader.readArrayStart();
    try {
      reader.readBooleanRaw();
      fail();
    } catch (MalformedJsonException expected) {
    }

    reader = new GsonReader(reader("[/* comment */ true]"));
    reader.readArrayStart();
    try {
      reader.readBooleanRaw();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testLenientComments() {
    GsonReader reader = new GsonReader(reader("[// comment \n true]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(true, reader.readBooleanRaw());

    reader = new GsonReader(reader("[# comment \n true]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(true, reader.readBooleanRaw());

    reader = new GsonReader(reader("[/* comment */ true]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(true, reader.readBooleanRaw());
  }

  public void testStrictCommentsWithSkipValue() {
    GsonReader reader = new GsonReader(reader("[// comment \n true]"));
    reader.readArrayStart();
    try {
      reader.skipValue();
      fail();
    } catch (MalformedJsonException expected) {
    }

    reader = new GsonReader(reader("[# comment \n true]"));
    reader.readArrayStart();
    try {
      reader.skipValue();
      fail();
    } catch (MalformedJsonException expected) {
    }

    reader = new GsonReader(reader("[/* comment */ true]"));
    reader.readArrayStart();
    try {
      reader.skipValue();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testStrictUnquotedNames() {
    GsonReader reader = new GsonReader(reader("{a:true}"));
    reader.readObjectStart();
    try {
      reader.readName();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testLenientUnquotedNames() {
    GsonReader reader = new GsonReader(reader("{a:true}"), true);
    //reader.setLenient(true);
    reader.readObjectStart();
    assertEquals("a", reader.readName());
  }

  public void testStrictUnquotedNamesWithSkipValue() {
    GsonReader reader = new GsonReader(reader("{a:true}"));
    reader.readObjectStart();
    try {
      reader.skipValue();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testStrictSingleQuotedNames() {
    GsonReader reader = new GsonReader(reader("{'a':true}"));
    reader.readObjectStart();
    try {
      reader.readName();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testLenientSingleQuotedNames() {
    GsonReader reader = new GsonReader(reader("{'a':true}"), true);
    //reader.setLenient(true);
    reader.readObjectStart();
    assertEquals("a", reader.readName());
  }

  public void testStrictSingleQuotedNamesWithSkipValue() {
    GsonReader reader = new GsonReader(reader("{'a':true}"));
    reader.readObjectStart();
    try {
      reader.skipValue();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testStrictUnquotedStrings() {
    GsonReader reader = new GsonReader(reader("[a]"));
    reader.readArrayStart();
    try {
      reader.readStringRaw();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testStrictUnquotedStringsWithSkipValue() {
    GsonReader reader = new GsonReader(reader("[a]"));
    reader.readArrayStart();
    try {
      reader.skipValue();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testLenientUnquotedStrings() {
    GsonReader reader = new GsonReader(reader("[a]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals("a", reader.readStringRaw());
  }

  public void testStrictSingleQuotedStrings() {
    GsonReader reader = new GsonReader(reader("['a']"));
    reader.readArrayStart();
    try {
      reader.readStringRaw();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testLenientSingleQuotedStrings() {
    GsonReader reader = new GsonReader(reader("['a']"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals("a", reader.readStringRaw());
  }

  public void testStrictSingleQuotedStringsWithSkipValue() {
    GsonReader reader = new GsonReader(reader("['a']"));
    reader.readArrayStart();
    try {
      reader.skipValue();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testStrictSemicolonDelimitedArray() {
    GsonReader reader = new GsonReader(reader("[true;true]"));
    reader.readArrayStart();
    try {
      reader.readBooleanRaw();
      reader.readBooleanRaw();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testLenientSemicolonDelimitedArray() {
    GsonReader reader = new GsonReader(reader("[true;true]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(true, reader.readBooleanRaw());
    assertEquals(true, reader.readBooleanRaw());
  }

  public void testStrictSemicolonDelimitedArrayWithSkipValue() {
    GsonReader reader = new GsonReader(reader("[true;true]"));
    reader.readArrayStart();
    try {
      reader.skipValue();
      reader.skipValue();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testStrictSemicolonDelimitedNameValuePair() {
    GsonReader reader = new GsonReader(reader("{\"a\":true;\"b\":true}"));
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    try {
      reader.readBooleanRaw();
      reader.readName();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testLenientSemicolonDelimitedNameValuePair() {
    GsonReader reader = new GsonReader(reader("{\"a\":true;\"b\":true}"), true);
    //reader.setLenient(true);
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    assertEquals(true, reader.readBooleanRaw());
    assertEquals("b", reader.readName());
  }

  public void testStrictSemicolonDelimitedNameValuePairWithSkipValue() {
    GsonReader reader = new GsonReader(reader("{\"a\":true;\"b\":true}"));
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    try {
      reader.skipValue();
      reader.skipValue();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testStrictUnnecessaryArraySeparators() {
    GsonReader reader = new GsonReader(reader("[true,,true]"));
    reader.readArrayStart();
    assertEquals(true, reader.readBooleanRaw());
    try {
      reader.readNullRaw();
      fail();
    } catch (MalformedJsonException expected) {
    }

    reader = new GsonReader(reader("[,true]"));
    reader.readArrayStart();
    try {
      reader.readNullRaw();
      fail();
    } catch (MalformedJsonException expected) {
    }

    reader = new GsonReader(reader("[true,]"));
    reader.readArrayStart();
    assertEquals(true, reader.readBooleanRaw());
    try {
      reader.readNullRaw();
      fail();
    } catch (MalformedJsonException expected) {
    }

    reader = new GsonReader(reader("[,]"));
    reader.readArrayStart();
    try {
      reader.readNullRaw();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testLenientUnnecessaryArraySeparators() {
    GsonReader reader = new GsonReader(reader("[true,,true]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(true, reader.readBooleanRaw());
    reader.readNullRaw();
    assertEquals(true, reader.readBooleanRaw());
    reader.readArrayEnd();

    reader = new GsonReader(reader("[,true]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    reader.readNullRaw();
    assertEquals(true, reader.readBooleanRaw());
    reader.readArrayEnd();

    reader = new GsonReader(reader("[true,]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(true, reader.readBooleanRaw());
    reader.readNullRaw();
    reader.readArrayEnd();

    reader = new GsonReader(reader("[,]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    reader.readNullRaw();
    reader.readNullRaw();
    reader.readArrayEnd();
  }

  public void testStrictUnnecessaryArraySeparatorsWithSkipValue() {
    GsonReader reader = new GsonReader(reader("[true,,true]"));
    reader.readArrayStart();
    assertEquals(true, reader.readBooleanRaw());
    try {
      reader.skipValue();
      fail();
    } catch (MalformedJsonException expected) {
    }

    reader = new GsonReader(reader("[,true]"));
    reader.readArrayStart();
    try {
      reader.skipValue();
      fail();
    } catch (MalformedJsonException expected) {
    }

    reader = new GsonReader(reader("[true,]"));
    reader.readArrayStart();
    assertEquals(true, reader.readBooleanRaw());
    try {
      reader.skipValue();
      fail();
    } catch (MalformedJsonException expected) {
    }

    reader = new GsonReader(reader("[,]"));
    reader.readArrayStart();
    try {
      reader.skipValue();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testStrictMultipleTopLevelValues() {
    GsonReader reader = new GsonReader(reader("[] []"));
    reader.readArrayStart();
    reader.readArrayEnd();
    try {
      reader.peek();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testLenientMultipleTopLevelValues() {
    GsonReader reader = new GsonReader(reader("[] true {}"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    reader.readArrayEnd();
    assertEquals(true, reader.readBooleanRaw());
    reader.readObjectStart();
    reader.readObjectEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testStrictMultipleTopLevelValuesWithSkipValue() {
    GsonReader reader = new GsonReader(reader("[] []"));
    reader.readArrayStart();
    reader.readArrayEnd();
    try {
      reader.skipValue();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testTopLevelValueTypes() {
    GsonReader reader1 = new GsonReader(reader("true"));
    assertTrue(reader1.readBooleanRaw());
    assertEquals(JsonToken.END_DOCUMENT, reader1.peek());

    GsonReader reader2 = new GsonReader(reader("false"));
    assertFalse(reader2.readBooleanRaw());
    assertEquals(JsonToken.END_DOCUMENT, reader2.peek());

    GsonReader reader3 = new GsonReader(reader("null"));
    assertEquals(JsonToken.NULL, reader3.peek());
    reader3.readNullRaw();
    assertEquals(JsonToken.END_DOCUMENT, reader3.peek());

    GsonReader reader4 = new GsonReader(reader("123"));
    assertEquals(123, reader4.nextInt());
    assertEquals(JsonToken.END_DOCUMENT, reader4.peek());

    GsonReader reader5 = new GsonReader(reader("123.4"));
    assertEquals(123.4, reader5.nextDouble());
    assertEquals(JsonToken.END_DOCUMENT, reader5.peek());

    GsonReader reader6 = new GsonReader(reader("\"a\""));
    assertEquals("a", reader6.readStringRaw());
    assertEquals(JsonToken.END_DOCUMENT, reader6.peek());
  }

  public void testTopLevelValueTypeWithSkipValue() {
    GsonReader reader = new GsonReader(reader("true"));
    reader.skipValue();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testStrictNonExecutePrefix() {
    GsonReader reader = new GsonReader(reader(")]}'\n []"));
    try {
      reader.readArrayStart();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testStrictNonExecutePrefixWithSkipValue() {
    GsonReader reader = new GsonReader(reader(")]}'\n []"));
    try {
      reader.skipValue();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testLenientNonExecutePrefix() {
    GsonReader reader = new GsonReader(reader(")]}'\n []"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    reader.readArrayEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testLenientNonExecutePrefixWithLeadingWhitespace() {
    GsonReader reader = new GsonReader(reader("\r\n \t)]}'\n []"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    reader.readArrayEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testLenientPartialNonExecutePrefix() {
    GsonReader reader = new GsonReader(reader(")]}' []"), true);
    //reader.setLenient(true);
    try {
      assertEquals(")", reader.readStringRaw());
      reader.readStringRaw();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testBomIgnoredAsFirstCharacterOfDocument() {
    GsonReader reader = new GsonReader(reader("\ufeff[]"));
    reader.readArrayStart();
    reader.readArrayEnd();
  }

  public void testBomForbiddenAsOtherCharacterInDocument() {
    GsonReader reader = new GsonReader(reader("[\ufeff]"));
    reader.readArrayStart();
    try {
      reader.readArrayEnd();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testFailWithPosition() {
    testFailWithPosition("Expected value at line 6 column 5 path $[1]",
        "[\n\n\n\n\n\"a\",}]");
  }

  public void testFailWithPositionGreaterThanBufferSize() {
    String spaces = repeat(' ', 8192);
    testFailWithPosition("Expected value at line 6 column 5 path $[1]",
        "[\n\n" + spaces + "\n\n\n\"a\",}]");
  }

  public void testFailWithPositionOverSlashSlashEndOfLineComment() {
    testFailWithPosition("Expected value at line 5 column 6 path $[1]",
        "\n// foo\n\n//bar\r\n[\"a\",}");
  }

  public void testFailWithPositionOverHashEndOfLineComment() {
    testFailWithPosition("Expected value at line 5 column 6 path $[1]",
        "\n# foo\n\n#bar\r\n[\"a\",}");
  }

  public void testFailWithPositionOverCStyleComment() {
    testFailWithPosition("Expected value at line 6 column 12 path $[1]",
        "\n\n/* foo\n*\n*\r\nbar */[\"a\",}");
  }

  public void testFailWithPositionOverQuotedString() {
    testFailWithPosition("Expected value at line 5 column 3 path $[1]",
        "[\"foo\nbar\r\nbaz\n\",\n  }");
  }

  public void testFailWithPositionOverUnquotedString() {
    testFailWithPosition("Expected value at line 5 column 2 path $[1]", "[\n\nabcd\n\n,}");
  }

  public void testFailWithEscapedNewlineCharacter() {
    testFailWithPosition("Expected value at line 5 column 3 path $[1]", "[\n\n\"\\\n\n\",}");
  }

  public void testFailWithPositionIsOffsetByBom() {
    testFailWithPosition("Expected value at line 1 column 6 path $[1]",
        "\ufeff[\"a\",}]");
  }

  private void testFailWithPosition(String message, String json) {
    // Validate that it works reading the string normally.
    GsonReader reader1 = new GsonReader(reader(json), true);
    //reader1.setLenient(true);
    reader1.readArrayStart();
    reader1.readStringRaw();
    try {
      reader1.peek();
      fail();
    } catch (MalformedJsonException expected) {
      assertEquals(message, expected.getMessage());
    }

    // Also validate that it works when skipping.
    GsonReader reader2 = new GsonReader(reader(json), true);
    //reader2.setLenient(true);
    reader2.readArrayStart();
    reader2.skipValue();
    try {
      reader2.peek();
      fail();
    } catch (MalformedJsonException expected) {
      assertEquals(message, expected.getMessage());
    }
  }

  public void testFailWithPositionDeepPath() {
    GsonReader reader = new GsonReader(reader("[1,{\"a\":[2,3,}"));
    reader.readArrayStart();
    reader.nextInt();
    reader.readObjectStart();
    reader.readName();
    reader.readArrayStart();
    reader.nextInt();
    reader.nextInt();
    try {
      reader.peek();
      fail();
    } catch (MalformedJsonException expected) {
      assertEquals("Expected value at line 1 column 14 path $[1].a[2]", expected.getMessage());
    }
  }

  public void testStrictVeryLongNumber() {
    GsonReader reader = new GsonReader(reader("[0." + repeat('9', 8192) + "]"));
    reader.readArrayStart();
    try {
      assertEquals(1d, reader.nextDouble());
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testLenientVeryLongNumber() {
    GsonReader reader = new GsonReader(reader("[0." + repeat('9', 8192) + "]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(JsonToken.STRING, reader.peek());
    assertEquals(1d, reader.nextDouble());
    reader.readArrayEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testVeryLongUnquotedLiteral() {
    String literal = "a" + repeat('b', 8192) + "c";
    GsonReader reader = new GsonReader(reader("[" + literal + "]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(literal, reader.readStringRaw());
    reader.readArrayEnd();
  }

  public void testDeeplyNestedArrays() {
    // this is nested 40 levels deep; Gson is tuned for nesting is 30 levels deep or fewer
    GsonReader reader = new GsonReader(reader(
        "[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]"));
    for (int i = 0; i < 40; i++) {
      reader.readArrayStart();
    }
    assertEquals("$[0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0]"
        + "[0][0][0][0][0][0][0][0][0][0][0][0][0][0]", reader.getPath());
    for (int i = 0; i < 40; i++) {
      reader.readArrayEnd();
    }
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testDeeplyNestedObjects() {
    // Build a JSON document structured like {"a":{"a":{"a":{"a":true}}}}, but 40 levels deep
    String array = "{\"a\":%s}";
    String json = "true";
    for (int i = 0; i < 40; i++) {
      json = String.format(array, json);
    }

    GsonReader reader = new GsonReader(reader(json));
    for (int i = 0; i < 40; i++) {
      reader.readObjectStart();
      assertEquals("a", reader.readName());
    }
    assertEquals("$.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a"
        + ".a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a", reader.getPath());
    assertEquals(true, reader.readBooleanRaw());
    for (int i = 0; i < 40; i++) {
      reader.readObjectEnd();
    }
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  // http://code.google.com/p/google-gson/issues/detail?id=409
  public void testStringEndingInSlash() {
    GsonReader reader = new GsonReader(reader("/"), true);
    //reader.setLenient(true);
    try {
      reader.peek();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testDocumentWithCommentEndingInSlash() {
    GsonReader reader = new GsonReader(reader("/* foo *//"), true);
    //reader.setLenient(true);
    try {
      reader.peek();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testStringWithLeadingSlash() {
    GsonReader reader = new GsonReader(reader("/x"), true);
    //reader.setLenient(true);
    try {
      reader.peek();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testUnterminatedObject() {
    GsonReader reader = new GsonReader(reader("{\"a\":\"android\"x"), true);
    //reader.setLenient(true);
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    assertEquals("android", reader.readStringRaw());
    try {
      reader.peek();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testVeryLongQuotedString() {
    char[] stringChars = new char[1024 * 16];
    Arrays.fill(stringChars, 'x');
    String string = new String(stringChars);
    String json = "[\"" + string + "\"]";
    GsonReader reader = new GsonReader(reader(json));
    reader.readArrayStart();
    assertEquals(string, reader.readStringRaw());
    reader.readArrayEnd();
  }

  public void testVeryLongUnquotedString() {
    char[] stringChars = new char[1024 * 16];
    Arrays.fill(stringChars, 'x');
    String string = new String(stringChars);
    String json = "[" + string + "]";
    GsonReader reader = new GsonReader(reader(json), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(string, reader.readStringRaw());
    reader.readArrayEnd();
  }

  public void testVeryLongUnterminatedString() {
    char[] stringChars = new char[1024 * 16];
    Arrays.fill(stringChars, 'x');
    String string = new String(stringChars);
    String json = "[" + string;
    GsonReader reader = new GsonReader(reader(json), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(string, reader.readStringRaw());
    try {
      reader.peek();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testSkipVeryLongUnquotedString() {
    GsonReader reader = new GsonReader(reader("[" + repeat('x', 8192) + "]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    reader.skipValue();
    reader.readArrayEnd();
  }

  public void testSkipTopLevelUnquotedString() {
    GsonReader reader = new GsonReader(reader(repeat('x', 8192)), true);
    //reader.setLenient(true);
    reader.skipValue();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testSkipVeryLongQuotedString() {
    GsonReader reader = new GsonReader(reader("[\"" + repeat('x', 8192) + "\"]"));
    reader.readArrayStart();
    reader.skipValue();
    reader.readArrayEnd();
  }

  public void testSkipTopLevelQuotedString() {
    GsonReader reader = new GsonReader(reader("\"" + repeat('x', 8192) + "\""), true);
    //reader.setLenient(true);
    reader.skipValue();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testStringAsNumberWithTruncatedExponent() {
    GsonReader reader = new GsonReader(reader("[123e]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(STRING, reader.peek());
  }

  public void testStringAsNumberWithDigitAndNonDigitExponent() {
    GsonReader reader = new GsonReader(reader("[123e4b]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(STRING, reader.peek());
  }

  public void testStringAsNumberWithNonDigitExponent() {
    GsonReader reader = new GsonReader(reader("[123eb]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(STRING, reader.peek());
  }

  public void testEmptyStringName() {
    GsonReader reader = new GsonReader(reader("{\"\":true}"), true);
    //reader.setLenient(true);
    assertEquals(BEGIN_OBJECT, reader.peek());
    reader.readObjectStart();
    assertEquals(NAME, reader.peek());
    assertEquals("", reader.readName());
    assertEquals(JsonToken.BOOLEAN, reader.peek());
    assertEquals(true, reader.readBooleanRaw());
    assertEquals(JsonToken.END_OBJECT, reader.peek());
    reader.readObjectEnd();
    assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testStrictExtraCommasInMaps() {
    GsonReader reader = new GsonReader(reader("{\"a\":\"b\",}"));
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    assertEquals("b", reader.readStringRaw());
    try {
      reader.peek();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testLenientExtraCommasInMaps() {
    GsonReader reader = new GsonReader(reader("{\"a\":\"b\",}"), true);
    //reader.setLenient(true);
    reader.readObjectStart();
    assertEquals("a", reader.readName());
    assertEquals("b", reader.readStringRaw());
    try {
      reader.peek();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  private String repeat(char c, int count) {
    char[] array = new char[count];
    Arrays.fill(array, c);
    return new String(array);
  }

  public void testMalformedDocuments() {
    assertDocument("{]", BEGIN_OBJECT, MalformedJsonException.class);
    assertDocument("{,", BEGIN_OBJECT, MalformedJsonException.class);
    assertDocument("{{", BEGIN_OBJECT, MalformedJsonException.class);
    assertDocument("{[", BEGIN_OBJECT, MalformedJsonException.class);
    assertDocument("{:", BEGIN_OBJECT, MalformedJsonException.class);
    assertDocument("{\"name\",", BEGIN_OBJECT, NAME, MalformedJsonException.class);
    assertDocument("{\"name\",", BEGIN_OBJECT, NAME, MalformedJsonException.class);
    assertDocument("{\"name\":}", BEGIN_OBJECT, NAME, MalformedJsonException.class);
    assertDocument("{\"name\"::", BEGIN_OBJECT, NAME, MalformedJsonException.class);
    assertDocument("{\"name\":,", BEGIN_OBJECT, NAME, MalformedJsonException.class);
    assertDocument("{\"name\"=}", BEGIN_OBJECT, NAME, MalformedJsonException.class);
    assertDocument("{\"name\"=>}", BEGIN_OBJECT, NAME, MalformedJsonException.class);
    assertDocument("{\"name\"=>\"string\":", BEGIN_OBJECT, NAME, STRING, MalformedJsonException.class);
    assertDocument("{\"name\"=>\"string\"=", BEGIN_OBJECT, NAME, STRING, MalformedJsonException.class);
    assertDocument("{\"name\"=>\"string\"=>", BEGIN_OBJECT, NAME, STRING, MalformedJsonException.class);
    assertDocument("{\"name\"=>\"string\",", BEGIN_OBJECT, NAME, STRING, MalformedJsonException.class);
    assertDocument("{\"name\"=>\"string\",\"name\"", BEGIN_OBJECT, NAME, STRING, NAME);
    assertDocument("[}", BEGIN_ARRAY, MalformedJsonException.class);
    assertDocument("[,]", BEGIN_ARRAY, NULL, NULL, END_ARRAY);
    assertDocument("{", BEGIN_OBJECT, MalformedJsonException.class);
    assertDocument("{\"name\"", BEGIN_OBJECT, NAME, MalformedJsonException.class);
    assertDocument("{\"name\",", BEGIN_OBJECT, NAME, MalformedJsonException.class);
    assertDocument("{'name'", BEGIN_OBJECT, NAME, MalformedJsonException.class);
    assertDocument("{'name',", BEGIN_OBJECT, NAME, MalformedJsonException.class);
    assertDocument("{name", BEGIN_OBJECT, NAME, MalformedJsonException.class);
    assertDocument("[", BEGIN_ARRAY, MalformedJsonException.class);
    assertDocument("[string", BEGIN_ARRAY, STRING, MalformedJsonException.class);
    assertDocument("[\"string\"", BEGIN_ARRAY, STRING, MalformedJsonException.class);
    assertDocument("['string'", BEGIN_ARRAY, STRING, MalformedJsonException.class);
    assertDocument("[123", BEGIN_ARRAY, NUMBER, MalformedJsonException.class);
    assertDocument("[123,", BEGIN_ARRAY, NUMBER, MalformedJsonException.class);
    assertDocument("{\"name\":123", BEGIN_OBJECT, NAME, NUMBER, MalformedJsonException.class);
    assertDocument("{\"name\":123,", BEGIN_OBJECT, NAME, NUMBER, MalformedJsonException.class);
    assertDocument("{\"name\":\"string\"", BEGIN_OBJECT, NAME, STRING, MalformedJsonException.class);
    assertDocument("{\"name\":\"string\",", BEGIN_OBJECT, NAME, STRING, MalformedJsonException.class);
    assertDocument("{\"name\":'string'", BEGIN_OBJECT, NAME, STRING, MalformedJsonException.class);
    assertDocument("{\"name\":'string',", BEGIN_OBJECT, NAME, STRING, MalformedJsonException.class);
    assertDocument("{\"name\":false", BEGIN_OBJECT, NAME, BOOLEAN, MalformedJsonException.class);
    assertDocument("{\"name\":false,,", BEGIN_OBJECT, NAME, BOOLEAN, MalformedJsonException.class);
  }

  /**
   * This test behave slightly differently in Gson 2.2 and earlier. It fails
   * during peek rather than during nextString().
   */
  public void testUnterminatedStringFailure() {
    GsonReader reader = new GsonReader(reader("[\"string"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    assertEquals(JsonToken.STRING, reader.peek());
    try {
      reader.readStringRaw();
      fail();
    } catch (MalformedJsonException expected) {
    }
  }

  private void assertDocument(String document, Object... expectations) {
    GsonReader reader = new GsonReader(reader(document), true);
    //reader.setLenient(true);
    for (Object expectation : expectations) {
      if (expectation == BEGIN_OBJECT) {
        reader.readObjectStart();
      } else if (expectation == BEGIN_ARRAY) {
        reader.readArrayStart();
      } else if (expectation == END_OBJECT) {
        reader.readObjectEnd();
      } else if (expectation == END_ARRAY) {
        reader.readArrayEnd();
      } else if (expectation == NAME) {
        assertEquals("name", reader.readName());
      } else if (expectation == BOOLEAN) {
        assertEquals(false, reader.readBooleanRaw());
      } else if (expectation == STRING) {
        assertEquals("string", reader.readStringRaw());
      } else if (expectation == NUMBER) {
        assertEquals(123, reader.nextInt());
      } else if (expectation == NULL) {
        reader.readNullRaw();
      } else if (expectation == MalformedJsonException.class) {
        try {
          reader.peek();
          fail();
        } catch (MalformedJsonException expected) {
        }
      } else {
        throw new AssertionError();
      }
    }
  }

  /**
   * Returns a reader that returns one character at a time.
   */
  private Reader reader(final String s) {
    /* if (true) */ return new StringReader(s);
    /* return new Reader() {
      int position = 0;
      @Override public int read(char[] buffer, int offset, int count) {
        if (position == s.length()) {
          return -1;
        } else if (count > 0) {
          buffer[offset] = s.charAt(position++);
          return 1;
        } else {
          throw new IllegalArgumentException();
        }
      }
      @Override public void close() {
      }
    }; */
  }
}
