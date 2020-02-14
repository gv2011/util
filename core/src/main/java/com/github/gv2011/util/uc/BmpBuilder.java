package com.github.gv2011.util.uc;

import com.github.gv2011.util.bytes.Bytes;

final class BmpBuilder extends DelegateBuilder{

  private final StringBuilder builder = new StringBuilder();

  BmpBuilder(){}

  @Override
  UStr build() {
    return new BmpString(builder.toString());
  }

  @Override
  void append(final int codePoint) {
    assert fits(codePoint);
    builder.appendCodePoint(codePoint);
  }

  @Override
  boolean fits(final int codePoint) {
    return codePoint<=UChar.MAX_BMP;
  }

  @Override
  void set(final int index, final int codePoint) {
    assert fits(codePoint);
    builder.setCharAt(index, (char) codePoint);
  }

  static UStr toHex(final Bytes bytes){
    final int s = size();
    final char[] result = new char[s==0?0:s*3-1];
    int i=0;
    for(final byte b: this){
      result[i*3] = HEX_CHARS.charAt((b>>4) & 0xF);
      result[i*3+1] = HEX_CHARS.charAt(b & 0xF);
      if(i<s-1)result[i*3+2] = ' ';
      i++;
    }
    return new BmpStrImp(result);
  }


}
