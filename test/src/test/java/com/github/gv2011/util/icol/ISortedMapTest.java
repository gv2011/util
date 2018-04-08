package com.github.gv2011.util.icol;

import static com.github.gv2011.testutil.Matchers.is;
import static com.github.gv2011.util.CollectionUtils.iCollections;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ISortedMapTest {

  @Test
  public void test() {
    final ISortedMap.Builder<String,String> b = iCollections().sortedMapBuilder();
    b.put("b", "lala");
    b.put("a", "lulu");
    final ISortedMap<String, String> map = b.build();
    assertThat(map.toString(), is("{a=lulu, b=lala}"));
  }

}
