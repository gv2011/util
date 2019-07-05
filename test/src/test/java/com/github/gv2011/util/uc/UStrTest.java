package com.github.gv2011.util.uc;

import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Matchers.is;
import static com.github.gv2011.util.Verify.verify;

import org.junit.Test;

public class UStrTest {

  @Test
  public void testAll() {
    for(int cp = Character.MIN_CODE_POINT; cp<=Character.MAX_CODE_POINT; cp++){
      verify(cp, Character::isValidCodePoint);
      if(UCharImp.isCharacter(cp)){
        final String str = UChars.toString(cp);
        final UStr ustr = UChars.uStr(str);
        assertThat(ustr.size(), is(1));
        assertThat(ustr.toString(), is(str));
      }
    }
  }

}
