package com.github.gv2011.util.bytes;

import static com.github.gv2011.testutil.Assert.assertFalse;
import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Assert.assertTrue;
import static com.github.gv2011.testutil.Matchers.is;

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
