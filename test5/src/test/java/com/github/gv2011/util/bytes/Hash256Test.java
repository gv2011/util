package com.github.gv2011.util.bytes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

class Hash256Test {

  @Test
  void test() {
    assertThat(
      ByteUtils.emptyBytes().hash().content(),
      is(ByteUtils.parseHex(
        "E3 B0 C4 42 98 FC 1C 14 9A FB F4 C8 99 6F B9 24 "+
        "27 AE 41 E4 64 9B 93 4C A4 95 99 1B 78 52 B8 55"
      ))
    );
  }

}
