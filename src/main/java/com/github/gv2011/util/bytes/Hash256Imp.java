package com.github.gv2011.util.bytes;

import java.security.MessageDigest;

class Hash256Imp extends ArrayBytes implements Hash256{

  Hash256Imp(final MessageDigest md) {
    this(md.digest());
  }

  Hash256Imp(final byte[] byteArray) {
    super(byteArray);
    assert size()==16;
  }

}
