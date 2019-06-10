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
import static com.github.gv2011.util.uc.Utf8Decoder.MASK_DATA_FOLLOW;
import static com.github.gv2011.util.uc.Utf8Decoder.MAX_FOUR;
import static com.github.gv2011.util.uc.Utf8Decoder.MAX_ONE;
import static com.github.gv2011.util.uc.Utf8Decoder.MAX_THREE;
import static com.github.gv2011.util.uc.Utf8Decoder.MAX_TWO;
import static com.github.gv2011.util.uc.Utf8Decoder.PREFIX_FOLLOW;
import static com.github.gv2011.util.uc.Utf8Decoder.PREFIX_FOUR;
import static com.github.gv2011.util.uc.Utf8Decoder.PREFIX_THREE;
import static com.github.gv2011.util.uc.Utf8Decoder.PREFIX_TWO;

import java.util.stream.IntStream;

final class Utf8Encoder {

  IntStream encode(final IntStream codePoints){
    return codePoints.flatMap(this::encodeNext);
  }

  private IntStream encodeNext(final int cp){
    final IntStream result;
    verify(cp>=0);
    if(cp<=MAX_ONE) result = IntStream.of(cp);
    else if(cp<=MAX_TWO){
      result = IntStream.of(
        PREFIX_TWO    | (cp>>6),
        PREFIX_FOLLOW | (cp & MASK_DATA_FOLLOW)
      );
    }
    else if(cp<=MAX_THREE){
      result = IntStream.of(
        PREFIX_THREE  | ( cp>>12),
        PREFIX_FOLLOW | ((cp>> 6) & MASK_DATA_FOLLOW),
        PREFIX_FOLLOW | ( cp      & MASK_DATA_FOLLOW)
      );
    }
    else{
      verify(cp<=MAX_FOUR);
      result = IntStream.of(
        PREFIX_FOUR   | ( cp>>18),
        PREFIX_FOLLOW | ((cp>>12) & MASK_DATA_FOLLOW),
        PREFIX_FOLLOW | ((cp>> 6) & MASK_DATA_FOLLOW),
        PREFIX_FOLLOW | ( cp      & MASK_DATA_FOLLOW)
      );
    }
    return result;
  }

}
