package com.github.gv2011.util.bytes;

/*-
 * %---license-start---
 * The MIT License (MIT)
 * %
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %
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
 * %---license-end---
 */



import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ByteUtilsTest {

  @Test
  public void testParseHex() {
    assertThat(ByteUtils.parseHex("  ").toString(), is(""));
    assertThat(ByteUtils.parseHex("01f2").toString(), is("01 F2"));
    assertThat(ByteUtils.parseHex(" f f\nAb ").toString(), is("FF AB"));
  }

  @Test
  public void testHash() {
    final Bytes b = ByteUtils.asUtf8("Franz jagt im komplett verwahrlosten Taxi quer durch Bayern");
    final Bytes expected = ByteUtils.parseHash(
      "d32b568cd1b96d459e7291ebf4b25d007f275c9f13149beeb782fac0716613f8"
    );
    assertThat(b.hash(), is(expected));
  }

  @Test
  public void testToInt() {
    final int[] examples = new int[]{0,1, Integer.MAX_VALUE, -1, Integer.MIN_VALUE};
    final Bytes[] expected = new Bytes[]{
      ByteUtils.parseHex("00 00 00 00"),
      ByteUtils.parseHex("00 00 00 01"),
      ByteUtils.parseHex("7F FF FF FF"),
      ByteUtils.parseHex("FF FF FF FF"),
      ByteUtils.parseHex("80 00 00 00")
    };
    for(int j=0; j<examples.length; j++) {
      final int intg = examples[j];
      final Bytes bytes = ByteUtils.asBytes(intg);
      assertThat(bytes, is(expected[j]));
      assertThat(Integer.toHexString(intg), bytes.toInt(), is(intg));
    }
    assertThat(ByteUtils.emptyBytes().toInt(), is(0));
    assertThat(ByteUtils.parseHex("FF").toInt(), is(-1));
  }


}
