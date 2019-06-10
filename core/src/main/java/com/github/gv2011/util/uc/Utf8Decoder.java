package com.github.gv2011.util.uc;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
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
import static com.github.gv2011.util.Verify.verify;

import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

import com.github.gv2011.util.CloseableIntIterator;

final class Utf8Decoder {

  private static final int MASK_PREFIX_ONE =      mask ("0xxxxxxx");
  private static final int PREFIX_ONE =           value("0xxxxxxx");
  static final int MAX_ONE =                      value("01111111");

  private static final int MASK_PREFIX_MULTIPLE = mask ("11xxxxxx");
  private static final int PREFIX_MULTIPLE =      value("11xxxxxx");

  private static final int MASK_PREFIX_TWO =      mask ("110xxxxx");
  static final int PREFIX_TWO =                   value("110xxxxx");
  static final int MAX_TWO =                      value(   "11111"+"111111");

  private static final int MASK_PREFIX_THREE =    mask ("1110xxxx");
  static final int PREFIX_THREE =                 value("1110xxxx");
  static final int MAX_THREE =                    value(    "1111"+"111111"+"111111");

  private static final int MASK_PREFIX_FOUR =     mask ("11110xxx");
  static final int PREFIX_FOUR =                  value("11110xxx");
  static final int MAX_FOUR =                     value(     "111"+"111111"+"111111"+"111111");

  private static final int MASK_DATA_TWO =        mask ("xxx11111");
  private static final int MASK_DATA_THREE =      mask ("xxxx1111");
  private static final int MASK_DATA_FOUR =       mask ("xxxxx111");
  static final int MASK_DATA_FOLLOW =     mask ("xx111111");

  private static final int MASK_PREFIX_FOLLOW =   mask ("10xxxxxx");
  static final int PREFIX_FOLLOW =        value("10xxxxxx");

  private static int mask(final String pattern) {
    return Integer.parseInt(pattern.replace('0', '1').replace('x', '0'), 2);
  }

  private static int value(final String pattern) {
    return Integer.parseInt(pattern.replace('x', '0'), 2);
  }


  IntStream decode(final CloseableIntIterator utf8){
    return
      IntStream.iterate(decodeNext(utf8), i->i!=-1, (i)->decodeNext(utf8))
      .onClose(utf8::close)
    ;
  }

  private int decodeNext(final PrimitiveIterator.OfInt utf8){
    final int result;
    if(!utf8.hasNext()) result = -1;
    else {
      final int b0 = readNext(utf8);
      if((b0 & MASK_PREFIX_ONE)==PREFIX_ONE) result = b0;
      else{// >=2 bytes
        verify((b0 & MASK_PREFIX_MULTIPLE)==PREFIX_MULTIPLE);
        final int b1 = readFollowing(utf8);
        if((b0 & MASK_PREFIX_TWO)==PREFIX_TWO) {
          result =
            ((b0 & MASK_DATA_TWO)<<6) |
            (b1 & MASK_DATA_FOLLOW)
          ;
        }
        else {
          final int b2 = readFollowing(utf8);
          if((b0 & MASK_PREFIX_THREE)==PREFIX_THREE) {
            result =
              ((b0 & MASK_DATA_THREE)<<12) |
              ((b1 & MASK_DATA_FOLLOW)<<6) |
              (b2 & MASK_DATA_FOLLOW)
            ;
          }
          else {
            verify((b0 & MASK_PREFIX_FOUR)==PREFIX_FOUR);
            final int b3 = readFollowing(utf8);
            result =
              ((b0 & MASK_DATA_FOUR  )<<18) |
              ((b1 & MASK_DATA_FOLLOW)<<12) |
              ((b2 & MASK_DATA_FOLLOW)<< 6) |
              (b3 & MASK_DATA_FOLLOW)
            ;
          }
        }
      }
    }
    return result;
  }

  private int readNext(final PrimitiveIterator.OfInt s){
    return Byte.toUnsignedInt((byte) s.nextInt());
  }

  private int readFollowing(final PrimitiveIterator.OfInt s){
    final int result = Byte.toUnsignedInt((byte) (byte) s.nextInt());
    verify((result & MASK_PREFIX_FOLLOW) == PREFIX_FOLLOW);
    return result;
  }

}
