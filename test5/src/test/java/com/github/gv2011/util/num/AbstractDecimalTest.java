package com.github.gv2011.util.num;

import static com.github.gv2011.util.icol.ICollections.listOf;
import static com.github.gv2011.util.num.NumUtils.num;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import com.github.gv2011.util.icol.IList;

class AbstractDecimalTest {

  @Test
  void testToBaseInt() {
    final IList<Intg> base3 = num(12).toBaseIntg(NumUtils.intg(3));
    assertThat(base3, is(listOf(num(1),num(1),num(0))));
  }

}
