package com.github.gv2011.util;

/*-
 * #%L
 * util-test
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */


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
