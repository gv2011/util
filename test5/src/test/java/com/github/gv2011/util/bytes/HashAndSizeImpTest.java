package com.github.gv2011.util.bytes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import com.github.gv2011.util.BeanUtils;

class HashAndSizeImpTest {

  @Test
  void testHashAndSizeImp() {
    final Bytes bytes = ByteUtils.emptyBytes();
    final HashAndSize hs1 = BeanUtils.beanBuilder(HashAndSize.class)
      .set(HashAndSize::hash).to(bytes.hash())
      .set(HashAndSize::size).to(bytes.longSize())
      .build()
    ;
    assertThat(hs1.getClass(), is(HashAndSizeImp.class));
    final HashAndSize hs2 = new HashAndSizeImp(bytes.hash(), bytes.longSize());
    assertThat(hs1, is(hs2));
  }

  @Test
  void testToString() {
    final Bytes bytes = ByteUtils.emptyBytes();
    final HashAndSize hs1 = BeanUtils.beanBuilder(HashAndSize.class)
      .set(HashAndSize::hash).to(bytes.hash())
      .set(HashAndSize::size).to(bytes.longSize())
      .build()
    ;
    assertThat(
      hs1.toString(),
      is(
        "HashAndSize{hash=application/x-sha-256:E3 B0 C4 42 98 FC 1C 14 9A FB F4 C8 99 6F B9 24 27 "+
        "AE 41 E4 64 9B 93 4C A4 95 99 1B 78 52 B8 55, size=0}"
      )
    );
    final HashAndSize hs2 = new HashAndSizeImp(bytes.hash(), bytes.longSize());
    assertThat(hs2.toString(), is(hs1.toString()));
  }
}
