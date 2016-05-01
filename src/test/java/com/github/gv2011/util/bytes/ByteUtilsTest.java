package com.github.gv2011.util.bytes;

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

}
