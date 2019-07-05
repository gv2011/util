package com.github.gv2011.util.bytes;

import static com.github.gv2011.testutil.Assert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

public class Base64Test {

  private String text;
  private String base64;

  @Before
  public void setup() {
    text = "Polyfon zwitschernd aßen Mäxchens Vögel Rüben, Joghurt und Quark";
    base64 = "UG9seWZvbiB6d2l0c2NoZXJuZCBhw59lbiBNw6R4Y2hlbnMgVs"
        + "O2Z2VsIFLDvGJlbiwgSm9naHVydCB1bmQgUXVhcms=";
  }

  @Test
  public void testEncode() {
    assertThat(ByteUtils.asUtf8(text).content().toBase64().utf8ToString(), is(base64));
  }

  @Test
  public void testDecode() {
    final TypedBytes base64Bytes = ByteUtils.asUtf8(base64);
    assertThat(base64Bytes.content().decodeBase64().utf8ToString(), is(text));
  }

}
