package com.github.gv2011.util.bytes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ByteScannerTest {

  @Test
  void testByteScanner() {
    final Bytes source = ByteUtils.newBytes(1,9,0,2,3);
    final Bytes sequence = ByteUtils.newBytes(9,0);
    final ByteScanner scanner = new ByteScanner(source.iterator(), sequence.iterator());

    assertTrue(scanner.hasNext());
    {
      final ByteIterator p = scanner.next();
      assertTrue(p.hasNext());
      assertThat(p.nextByte(), is((byte) 1));
      assertFalse(p.hasNext());
    }

    assertTrue(scanner.hasNext());
    {
      final ByteIterator p = scanner.next();
      assertTrue(p.hasNext());
      assertThat(p.nextByte(), is((byte) 2));
      assertTrue(p.hasNext());
      assertThat(p.nextByte(), is((byte) 3));
      assertFalse(p.hasNext());
    }

    assertFalse(scanner.hasNext());
  }

}
