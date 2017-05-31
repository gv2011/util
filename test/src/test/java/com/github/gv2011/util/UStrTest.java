package com.github.gv2011.util;

import static com.github.gv2011.testutil.Matchers.is;
import static com.github.gv2011.util.Verify.verify;
import static org.junit.Assert.*;

import org.junit.Test;

public class UStrTest {

  @Test
  public void testAll() {
    for(int cp = Character.MIN_CODE_POINT; cp<=Character.MAX_CODE_POINT; cp++){
      verify(cp, Character::isValidCodePoint);
      final String str = UChar.toString(cp);
      if(!UChar.isSurrogate(cp)){
        final UStr ustr = UStr.uStr(str);
        assertThat(ustr.size(), is(1));
        assertThat(ustr.toString(), is(str));
      }
    }
  }

}
