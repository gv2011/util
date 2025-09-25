package com.github.gv2011.util.bytes;

import static com.github.gv2011.testutil.Assert.assertFalse;
import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Assert.assertTrue;
import static com.github.gv2011.testutil.Matchers.is;

import org.junit.Test;

import com.github.gv2011.util.icol.Opt;

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
    assertThat(ByteUtils.parseHex("").indexOfOther(ByteUtils.parseHex("")), is(Opt.of(0L)));
    assertThat(ByteUtils.parseHex("").indexOfOther(ByteUtils.parseHex("01")), is(Opt.empty()));
    assertThat(ByteUtils.parseHex("01").indexOfOther(ByteUtils.parseHex("01")), is(Opt.of(0L)));
    assertThat(ByteUtils.parseHex("01").indexOfOther(ByteUtils.parseHex("0102")), is(Opt.empty()));
    assertThat(ByteUtils.parseHex("01020303").indexOfOther(ByteUtils.parseHex("0203")), is(Opt.of(1L)));
    assertThat(ByteUtils.parseHex("01020303").indexOfOther(ByteUtils.parseHex("020304")), is(Opt.empty()));
  }

}
