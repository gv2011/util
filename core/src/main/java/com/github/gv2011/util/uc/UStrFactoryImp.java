package com.github.gv2011.util.uc;

import static com.github.gv2011.util.Verify.verify;

import java.util.PrimitiveIterator.OfInt;
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
import java.util.function.IntUnaryOperator;

public final class UStrFactoryImp implements UStrFactory {

  @Override
  public UStr collect(final int size, final IntUnaryOperator valueForIndex) {
    final UStrBuilderImp builder = new UStrBuilderImp();
    for(int i=0; i<size; i++) builder.append(valueForIndex.applyAsInt(i));
    return builder.build();
  }

  @Override
  public UChar uChar(final int codePoint) {
    return UCharImp.uChar(codePoint);
  }

  @Override
  public UChar uChar(final String character) {
    final int codepoint;
    if(character.length()==1) codepoint = character.charAt(0);
    else {
      final OfInt codepoints = character.codePoints().iterator();
      codepoint = codepoints.nextInt();
      verify(!codepoints.hasNext());
    }
    return UCharImp.uChar(codepoint);
  }

  @Override
  public UStrBuilder uStrBuilder() {
    return new UStrBuilderImp();
  }

}
