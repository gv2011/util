package com.github.gv2011.util.bc;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;

class UnixSha512CryptHashImpTest {

  @Test
  void testUnixSha512CryptHashImpString() {
    final String password = "😀RootPassword";
    final UnixSha512CryptHashImp hash = new UnixSha512CryptHashImp(password);
    assertTrue(hash.verify(password));
    assertFalse(hash.verify("test"));
  }

  @Test
  void testUnixSha512CryptHashImpBytes() {
    final Bytes password = ByteUtils.asBytes(255);
    final UnixSha512CryptHashImp hash = new UnixSha512CryptHashImp(password);
    assertTrue(hash.verify(password));
    assertFalse(hash.verify("test"));
  }

}
