package com.github.gv2011.util.bytes;

import static com.github.gv2011.testutil.Matchers.is;
import static org.junit.Assert.*;

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
    assertThat(ByteUtils.parseHex("").indexOfOther(ByteUtils.parseHex("")), is(0L));
    assertThat(ByteUtils.parseHex("").indexOfOther(ByteUtils.parseHex("01")), is(-1L));
    assertThat(ByteUtils.parseHex("01").indexOfOther(ByteUtils.parseHex("01")), is(0L));
    assertThat(ByteUtils.parseHex("01").indexOfOther(ByteUtils.parseHex("0102")), is(-1L));
    assertThat(ByteUtils.parseHex("01020303").indexOfOther(ByteUtils.parseHex("0203")), is(1L));
    assertThat(ByteUtils.parseHex("01020303").indexOfOther(ByteUtils.parseHex("020304")), is(-1L));
  }

}
