package com.github.gv2011.util;

import static com.github.gv2011.util.icol.ICollections.emptyList;
import static com.github.gv2011.util.icol.ICollections.listOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.OptionalLong;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class CollectionUtilsTest {

  @Test
  void testFindFirstDifference() {
    assertThat(
      CollectionUtils.findFirstDifference(listOf(
        Stream.of("A"),
        Stream.of()
      )),
      is(OptionalLong.of(0))
    );
    assertThat(
      CollectionUtils.findFirstDifference(listOf(
        Stream.of("A", "B", "C"),
        Stream.of("A", "B", "D")
      )),
      is(OptionalLong.of(2))
    );
    assertThat(
      CollectionUtils.findFirstDifference(listOf(
        Stream.of("A", "B", "C"),
        Stream.of("A", "B", "C")
      )),
      is(OptionalLong.empty())
    );
    assertThat(
      CollectionUtils.findFirstDifference(emptyList()),
      is(OptionalLong.empty())
    );
  }

}
