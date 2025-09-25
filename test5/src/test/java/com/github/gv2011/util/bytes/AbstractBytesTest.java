package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.bytes.ByteUtils.newBytes;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import com.github.gv2011.util.icol.Opt;

class AbstractBytesTest {

  @Test
  void testFindStartOfOther() {
    final Bytes bytes = newBytes(1,2,3,4,5);
    assertThat(bytes.findStartOfOther(newBytes(1)),     is(Opt.of(0L)));
    assertThat(bytes.findStartOfOther(newBytes(3,4)),   is(Opt.of(2L)));
    assertThat(bytes.findStartOfOther(newBytes(5,6)),   is(Opt.of(4L)));
    assertThat(bytes.findStartOfOther(newBytes()),      is(Opt.of(0L)));
    assertThat(bytes.findStartOfOther(newBytes(2,3,5)), is(Opt.empty()));
    assertThat(bytes.findStartOfOther(newBytes(6)),     is(Opt.empty()));
  }

}
