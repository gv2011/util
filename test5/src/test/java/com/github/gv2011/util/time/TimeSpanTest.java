package com.github.gv2011.util.time;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.github.gv2011.util.tstr.TypedString;

class TimeSpanTest {

  @Test
  void test() {
    final TimeSpan ts = TimeSpan.create(
      Instant.parse("2022-12-28T09:15:53.702495Z"),
      Instant.parse("2022-12-28T09:15:55.202500Z")
    );
    assertThat(ts.toString(), is("(2022-12-28T09:15:53.702495Z,2022-12-28T09:15:55.202500Z)"));
    assertThat(
      TypedString.create(TimeSpan.class, ts.toString()),
      is(ts)
    );
  }

}
