package com.github.gv2011.util;


import static com.github.gv2011.testutil.Matchers.is;
import static com.github.gv2011.util.Verify.verify;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class UCharTest {

  @Test
  public void testAll() {
    for(int cp = 0x0; cp<=Character.MAX_CODE_POINT; cp++){
      verify(cp, Character::isValidCodePoint);
      final String str = UChar.toString(cp);
      if(!UChar.isSurrogate(cp)){
        assertThat(UChar.toCodepoint(str), is(cp));
        final UChar c = UChar.uChar(cp);
        c.type();
        assertThat(c.codePoint(), is(cp));
        assertThat(c.toString(), is(str));
      }
    }
  }

  @Test
  public void testUtf16() {
    System.out.print(UChar.uChar(0x20010));
    int col = 0;
    for(int cp = 0x20000; cp<0x20000; cp++){
      if(!UChar.isSurrogate(cp)){
        final UChar c = UChar.uChar(cp);
        //System.out.println(format("{}: \t'{}'", Integer.toHexString(cp), c));
        System.out.print(c);
        col++;
        if(col==96){System.out.println();col=0;}
      }
    }
  }

}
