package com.github.gv2011.util.bytes;

import static com.github.gv2011.testutil.Assert.assertThat;
import static org.hamcrest.Matchers.is;

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
    final TypedBytes b = ByteUtils.asUtf8("Franz jagt im komplett verwahrlosten Taxi quer durch Bayern");
    final Hash256 expected = ByteUtils.parseHash(
      "d32b568cd1b96d459e7291ebf4b25d007f275c9f13149beeb782fac0716613f8"
    );
    assertThat(b.content().hash(), is(expected));
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
