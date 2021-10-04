package com.github.gv2011.util.examples;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

class BufferExample {

  @Test
  void test() {
    final ByteBuffer b = ByteBuffer.allocate(10);
    assertThat(b.remaining(), is(10));
    assertThat(b.position(), is(0));
    assertThat(b.limit(), is(10));

    b.put((byte)77);
    assertThat(b.remaining(), is(9));
    assertThat(b.position(), is(1));
    assertThat(b.limit(), is(10));

    b.put((byte)88);
    assertThat(b.remaining(), is(8));
    assertThat(b.position(), is(2));
    assertThat(b.limit(), is(10));

    b.flip();
    assertThat(b.remaining(), is(2));
    assertThat(b.position(), is(0));
    assertThat(b.limit(), is(2));

    assertThat(b.get(), is((byte)77));
    assertThat(b.remaining(), is(1));
    assertThat(b.position(), is(1));
    assertThat(b.limit(), is(2));

    b.mark();
    b.position(b.limit());
    b.limit(b.capacity());
    assertThat(b.remaining(), is(8));
    assertThat(b.position(), is(2));
    assertThat(b.limit(), is(10));

    b.put((byte)99);
    assertThat(b.remaining(), is(7));
    assertThat(b.position(), is(3));
    assertThat(b.limit(), is(10));

    b.limit(b.position());
    b.reset();
    assertThat(b.remaining(), is(2));
    assertThat(b.position(), is(1));
    assertThat(b.limit(), is(3));

    assertThat(b.get(), is((byte)88));
    assertThat(b.get(), is((byte)99));
    assertThat(b.remaining(), is(0));
    assertThat(b.position(), is(3));
    assertThat(b.limit(), is(3));
 }


}
