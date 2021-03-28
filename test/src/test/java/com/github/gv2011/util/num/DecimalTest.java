package com.github.gv2011.util.num;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class DecimalTest {

  @Test
  public void testToEcmaString() {
    assertThat(NumUtils.parse("0").toEcmaString(), is("0"));
    assertThat(NumUtils.parse("1").toEcmaString(), is("1"));
    assertThat(NumUtils.parse("0.0000001").toEcmaString(), is("1e-7"));
    assertThat(NumUtils.parse("-0.000001").toEcmaString(), is("-0.000001"));
    assertThat(NumUtils.parse("0.0000012345").toEcmaString(), is("0.0000012345"));
    assertThat(NumUtils.parse( "100000000000000000000").toEcmaString(), is("100000000000000000000"));
    assertThat(NumUtils.parse("1000000000000000000000").toEcmaString(), is("1e+21"));
    assertThat(NumUtils.parse("1000000000000000000001e-10").toEcmaString(), is("100000000000.0000000001"));
    assertThat(NumUtils.parse("12e-8").toEcmaString(), is("1.2e-7"));
    assertThat(NumUtils.parse("-123e-9").toEcmaString(), is("-1.23e-7"));
  }

}
