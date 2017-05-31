package com.github.gv2011.util.loc;

import static java.util.stream.Collectors.joining;

import org.junit.Test;

public class CountryTest {

  @Test
  public void test() {
    System.out.println(Country.countries().stream()
      .map(c->c.toString()+" "+c.name())
      .collect(joining("\n"))
    );
  }

}
