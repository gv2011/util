package com.github.gv2011.util.icol;

import static com.github.gv2011.testutil5.Matchers5.meets;
import static com.github.gv2011.util.icol.ICollections.setOf;
import static com.github.gv2011.util.icol.SpliteratorCharacteristic.CONCURRENT;
import static com.github.gv2011.util.icol.SpliteratorCharacteristic.DISTINCT;
import static com.github.gv2011.util.icol.SpliteratorCharacteristic.IMMUTABLE;
import static com.github.gv2011.util.icol.SpliteratorCharacteristic.NONNULL;
import static com.github.gv2011.util.icol.SpliteratorCharacteristic.SIZED;
import static com.github.gv2011.util.icol.SpliteratorCharacteristic.SUBSIZED;
import static com.github.gv2011.util.icol.SpliteratorCharacteristic.characteristics;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ISetTest {

  @Test
  void testParallelStream() {
    assertTrue(setOf("B", "A", "C").parallelStream().isParallel());
  }

  @Test
  void testSpliterator() {
    final ISortedSet<SpliteratorCharacteristic> ch = characteristics(ICollections.setOf("B", "A", "C").spliterator());
    assertThat(
      ch,
      meets(c->
        c.containsAll(setOf(DISTINCT, IMMUTABLE, NONNULL, SIZED, SUBSIZED)) &&
        !c.contains(CONCURRENT)
      )
    );
  }

}
