package com.github.gv2011.util.uc;

import org.junit.Test;

public class UStrImpTest {

  @Test
  public void testSplit() {
    new BmpString(",a,b,c").split(UChars.uChar(','));
  }

}
