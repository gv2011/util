package com.github.gv2011.util.num;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class IntDecimalTest {

  @Test
  public void testMultiply() {
    final int max = Integer.MAX_VALUE;
    final long product = BigInteger.valueOf(max).pow(2).longValueExact();
    assertThat(
      (long)max * (long)max,
      is(product)
    );
    assertThat(
      NumUtils.num(max).multiply(NumUtils.num(max)).longValue(),
      is(product)
    );
  }

}
