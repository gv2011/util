package com.github.gv2011.util.gcol;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.gv2011.util.icol.IList;
import com.google.common.collect.ImmutableList;

public class IListWrapperTest {

  @Test
  public void testEmptySubList() {
    final IList<String> empty = IListWrapper.wrap(ImmutableList.of("a","b","c")).subList(1, 1);
    assertTrue(empty.isEmpty());
  }

}
