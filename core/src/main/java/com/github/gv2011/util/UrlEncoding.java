package com.github.gv2011.util;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2019 Vinz (https://github.com/gv2011)
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

import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.BytesBuilder;

public final class UrlEncoding {

  private UrlEncoding(){staticClass();}

  public static String decode(final String raw) {
    final BytesBuilder result = ByteUtils.newBytesBuilder();
    final int[] chars = raw.codePoints().toArray();
    int i = 0;
    final int length = chars.length;
    while(i<length){
      final int c = chars[i++];
      if(c=='%' && i+1<length){
        {
          final int d1 = Character.digit(chars[i], 16);
          final int d2 = Character.digit(chars[i+1], 16);
          if(d1!=-1 && d2!=-1){
            i+=2;
            result.write((d1*16)+d2);
          }
          else result.write(c);
        }
      }
      else{
        if(c<0x80) result.write(c);
        else result.write(new String(Character.toChars(c)).getBytes(UTF_8));
      }
    }
    return result.build().utf8ToString();
  }

}
