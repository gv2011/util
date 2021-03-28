/*
 * Copyright (C) 2014 Google Inc.
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

import java.io.StringReader;

import junit.framework.TestCase;

public class JsonReaderPathTest extends TestCase {
  public void testPath() {
    GsonReader reader = new GsonReader(
        new StringReader("{\"a\":[2,true,false,null,\"b\",{\"c\":\"d\"},[3]]}"));
    assertEquals("$", reader.getPath());
    reader.readObjectStart();
    assertEquals("$.", reader.getPath());
    reader.readName();
    assertEquals("$.a", reader.getPath());
    reader.readArrayStart();
    assertEquals("$.a[0]", reader.getPath());
    reader.nextInt();
    assertEquals("$.a[1]", reader.getPath());
    reader.readBooleanRaw();
    assertEquals("$.a[2]", reader.getPath());
    reader.readBooleanRaw();
    assertEquals("$.a[3]", reader.getPath());
    reader.readNullRaw();
    assertEquals("$.a[4]", reader.getPath());
    reader.readStringRaw();
    assertEquals("$.a[5]", reader.getPath());
    reader.readObjectStart();
    assertEquals("$.a[5].", reader.getPath());
    reader.readName();
    assertEquals("$.a[5].c", reader.getPath());
    reader.readStringRaw();
    assertEquals("$.a[5].c", reader.getPath());
    reader.readObjectEnd();
    assertEquals("$.a[6]", reader.getPath());
    reader.readArrayStart();
    assertEquals("$.a[6][0]", reader.getPath());
    reader.nextInt();
    assertEquals("$.a[6][1]", reader.getPath());
    reader.readArrayEnd();
    assertEquals("$.a[7]", reader.getPath());
    reader.readArrayEnd();
    assertEquals("$.a", reader.getPath());
    reader.readObjectEnd();
    assertEquals("$", reader.getPath());
  }

  public void testObjectPath() {
    GsonReader reader = new GsonReader(new StringReader("{\"a\":1,\"b\":2}"));
    assertEquals("$", reader.getPath());

    reader.peek();
    assertEquals("$", reader.getPath());
    reader.readObjectStart();
    assertEquals("$.", reader.getPath());

    reader.peek();
    assertEquals("$.", reader.getPath());
    reader.readName();
    assertEquals("$.a", reader.getPath());

    reader.peek();
    assertEquals("$.a", reader.getPath());
    reader.nextInt();
    assertEquals("$.a", reader.getPath());

    reader.peek();
    assertEquals("$.a", reader.getPath());
    reader.readName();
    assertEquals("$.b", reader.getPath());

    reader.peek();
    assertEquals("$.b", reader.getPath());
    reader.nextInt();
    assertEquals("$.b", reader.getPath());

    reader.peek();
    assertEquals("$.b", reader.getPath());
    reader.readObjectEnd();
    assertEquals("$", reader.getPath());

    reader.peek();
    assertEquals("$", reader.getPath());
    reader.close();
    assertEquals("$", reader.getPath());
  }

  public void testArrayPath() {
    GsonReader reader = new GsonReader(new StringReader("[1,2]"));
    assertEquals("$", reader.getPath());

    reader.peek();
    assertEquals("$", reader.getPath());
    reader.readArrayStart();
    assertEquals("$[0]", reader.getPath());

    reader.peek();
    assertEquals("$[0]", reader.getPath());
    reader.nextInt();
    assertEquals("$[1]", reader.getPath());

    reader.peek();
    assertEquals("$[1]", reader.getPath());
    reader.nextInt();
    assertEquals("$[2]", reader.getPath());

    reader.peek();
    assertEquals("$[2]", reader.getPath());
    reader.readArrayEnd();
    assertEquals("$", reader.getPath());

    reader.peek();
    assertEquals("$", reader.getPath());
    reader.close();
    assertEquals("$", reader.getPath());
  }

  public void testMultipleTopLevelValuesInOneDocument() {
    GsonReader reader = new GsonReader(new StringReader("[][]"), true);
    //reader.setLenient(true);
    reader.readArrayStart();
    reader.readArrayEnd();
    assertEquals("$", reader.getPath());
    reader.readArrayStart();
    reader.readArrayEnd();
    assertEquals("$", reader.getPath());
  }

  public void testSkipArrayElements() {
    GsonReader reader = new GsonReader(new StringReader("[1,2,3]"));
    reader.readArrayStart();
    reader.skipValue();
    reader.skipValue();
    assertEquals("$[2]", reader.getPath());
  }

  public void testSkipObjectNames() {
    GsonReader reader = new GsonReader(new StringReader("{\"a\":1}"));
    reader.readObjectStart();
    reader.skipValue();
    assertEquals("$.null", reader.getPath());
  }

  public void testSkipObjectValues() {
    GsonReader reader = new GsonReader(new StringReader("{\"a\":1,\"b\":2}"));
    reader.readObjectStart();
    reader.readName();
    reader.skipValue();
    assertEquals("$.null", reader.getPath());
    reader.readName();
    assertEquals("$.b", reader.getPath());
  }

  public void testSkipNestedStructures() {
    GsonReader reader = new GsonReader(new StringReader("[[1,2,3],4]"));
    reader.readArrayStart();
    reader.skipValue();
    assertEquals("$[1]", reader.getPath());
  }

  public void testArrayOfObjects() {
    GsonReader reader = new GsonReader(new StringReader("[{},{},{}]"));
    reader.readArrayStart();
    assertEquals("$[0]", reader.getPath());
    reader.readObjectStart();
    assertEquals("$[0].", reader.getPath());
    reader.readObjectEnd();
    assertEquals("$[1]", reader.getPath());
    reader.readObjectStart();
    assertEquals("$[1].", reader.getPath());
    reader.readObjectEnd();
    assertEquals("$[2]", reader.getPath());
    reader.readObjectStart();
    assertEquals("$[2].", reader.getPath());
    reader.readObjectEnd();
    assertEquals("$[3]", reader.getPath());
    reader.readArrayEnd();
    assertEquals("$", reader.getPath());
  }

  public void testArrayOfArrays() {
    GsonReader reader = new GsonReader(new StringReader("[[],[],[]]"));
    reader.readArrayStart();
    assertEquals("$[0]", reader.getPath());
    reader.readArrayStart();
    assertEquals("$[0][0]", reader.getPath());
    reader.readArrayEnd();
    assertEquals("$[1]", reader.getPath());
    reader.readArrayStart();
    assertEquals("$[1][0]", reader.getPath());
    reader.readArrayEnd();
    assertEquals("$[2]", reader.getPath());
    reader.readArrayStart();
    assertEquals("$[2][0]", reader.getPath());
    reader.readArrayEnd();
    assertEquals("$[3]", reader.getPath());
    reader.readArrayEnd();
    assertEquals("$", reader.getPath());
  }
}
