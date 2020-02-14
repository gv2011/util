package com.github.gv2011.util.uc;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class Gv1Charset extends Charset{

  static final byte ESCAPE_1_BYTE = 1;
  static final byte ESCAPE_2_BYTES = 2;
  static final byte ESCAPE_3_BYTES = 3;


  protected Gv1Charset(final String canonicalName, final String[] aliases) {
    super("gv2011-01", null);
  }

  @Override
  public boolean contains(final Charset cs) {
    return true;
  }

  @Override
  public CharsetDecoder newDecoder() {
    // TODO Auto-generated method stub
    return new GvCharsetDecoder(this);
  }

  @Override
  public CharsetEncoder newEncoder() {
    // TODO Auto-generated method stub
    return null;
  }

}
