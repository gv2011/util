package com.github.gv2011.util.bytes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

class ByteUtilsTest {

  @Test
  void testToBytes() {
    assertThat(ByteUtils.toBytes(1L).toHex(),             is("0000000000000001"));
    assertThat(ByteUtils.toBytes(Long.MAX_VALUE).toHex(), is("7fffffffffffffff"));
    assertThat(ByteUtils.toBytes(-1L).toHex(),            is("ffffffffffffffff"));
  }

}
