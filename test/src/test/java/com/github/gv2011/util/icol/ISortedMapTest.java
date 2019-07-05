package com.github.gv2011.util.icol;

import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Matchers.is;
import static com.github.gv2011.util.icol.ICollections.sortedMapBuilder;

import org.junit.Test;

public class ISortedMapTest {

  @Test
  public void test() {
    final ISortedMap.Builder<String,String> b = sortedMapBuilder();
    b.put("b", "lala");
    b.put("a", "lulu");
    final ISortedMap<String, String> map = b.build();
    assertThat(map.toString(), is("{a=lulu, b=lala}"));
  }

}
