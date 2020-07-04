package com.github.gv2011.util.uc;

import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Matchers.is;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.format;

import org.junit.Test;

public class UCharTest {

  @Test
  public void testAll() {
    for(int cp = 0x0; cp<=Character.MAX_CODE_POINT; cp++){
      verify(cp, Character::isValidCodePoint);
      if(cp<=Character.MAX_VALUE ? !Character.isSurrogate((char) cp) : true){
        final String str = UChars.toString(cp);
        if(!UChars.isSurrogate(cp)){
          assertThat(UChars.toCodepoint(str), is(cp));
          final UChar c = UChars.uChar(cp);
          c.type();
          assertThat(c.codePoint(), is(cp));
          assertThat(c.toString(), is(str));
        }
      }
    }
  }

  @Test
  public void testUtf16() {
    System.out.print(UChars.uChar(0x20010));
    int col = 0;
    for(int cp = 0x20000; cp<0x20000; cp++){
      if(!UChars.isSurrogate(cp)){
        final UChar c = UChars.uChar(cp);
        System.out.println(format("{}: \t'{}'", Integer.toHexString(cp), c));
        System.out.print(c);
        col++;
        if(col==96){System.out.println();col=0;}
      }
    }
  }

}
