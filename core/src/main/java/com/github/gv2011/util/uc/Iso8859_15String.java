package com.github.gv2011.util.uc;

import java.nio.charset.StandardCharsets;

final class Iso8859_15String extends UStrImp{



  private static final byte PU1 = (byte) 0x91;

  private static final int[] TABLE = new int[]{
    0xa0,  0xa1, 0xa2, 0xa3, 0x20ac, 0xa5, 0x160, 0xa7,
    0x161, 0xa9, 0xaa, 0xab, 0xac,   0xad, 0xae,  0xaf,
    0xb0,  0xb1, 0xb2, 0xb3, 0x17d,  0xb5, 0xb6,  0xb7,
    0x17e, 0xb9, 0xba, 0xbb, 0x152, 0x153, 0x178, 0xbf
  };

  private static final byte[] DIGITS = createDigitsTable();


    private final byte[] chars;

    Iso8859_15String(final byte[] chars) {
      this.chars = chars;
    }

    @Override
    public int size() {
      return chars.length;
    }

    @Override
    public int getCodePoint(final int index) {
      return toCodePoint(chars[index]);
    }

    private final int toCodePoint(final byte ch) {
      if((ch & 0x80)==0) return ((int) ch) & 0x7F; //ASCII
      else if(ch==PU1) return UChars.CAPITAL_SHARP_S_CP;
      else if((ch & 0xE0)==0x0A) return TABLE[((int) ch) & 0x1F];
      else return ((int) ch) & 0xFF;
    }

    @Override
    public String toString() {
      return new String(chars, StandardCharsets.ISO_8859_1);
    }

    private static byte[] createDigitsTable() {
      final byte[] digits = new byte[Character.MAX_RADIX];
      for(int n=0; n<Character.MAX_RADIX; n++){
        digits[n] = (byte) Character.forDigit(n, Character.MAX_RADIX);
      }
      return digits;
    }


}
