package com.github.gv2011.util.bytes;

/*-
 * #%L
 * util-test
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
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

import static com.github.gv2011.testutil.Matchers.is;
import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;

public class BytesTest {

  @Test
  public void testStartsWith() {
    assertTrue(ByteUtils.parseHex("").startsWith(ByteUtils.parseHex("")));
    assertTrue(ByteUtils.parseHex("010203").startsWith(ByteUtils.parseHex("")));
    assertTrue(ByteUtils.parseHex("010203").startsWith(ByteUtils.parseHex("0102")));
    assertTrue(ByteUtils.parseHex("010203").startsWith(ByteUtils.parseHex("010203")));
    assertFalse(ByteUtils.parseHex("010203").startsWith(ByteUtils.parseHex("01020304")));
  }

  @Test
  public void testIndexOfOther() {
    assertThat(ByteUtils.parseHex("").indexOfOther(ByteUtils.parseHex("")), is(Optional.of(0L)));
    assertThat(ByteUtils.parseHex("").indexOfOther(ByteUtils.parseHex("01")), is(Optional.empty()));
    assertThat(ByteUtils.parseHex("01").indexOfOther(ByteUtils.parseHex("01")), is(Optional.of(0L)));
    assertThat(ByteUtils.parseHex("01").indexOfOther(ByteUtils.parseHex("0102")), is(Optional.empty()));
    assertThat(ByteUtils.parseHex("01020303").indexOfOther(ByteUtils.parseHex("0203")), is(Optional.of(1L)));
    assertThat(ByteUtils.parseHex("01020303").indexOfOther(ByteUtils.parseHex("020304")), is(Optional.empty()));
  }

}
