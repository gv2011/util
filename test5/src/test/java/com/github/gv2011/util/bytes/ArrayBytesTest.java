package com.github.gv2011.util.bytes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

class ArrayBytesTest {

  @Test
  void testToBigInteger() {
    assertThat(ArrayBytes.create(new byte[]{0}).toBigInteger(), is(BigInteger.ZERO));
    assertThat(ArrayBytes.create(new byte[]{-1}).toBigInteger(), is(BigInteger.valueOf(255)));
  }

}
