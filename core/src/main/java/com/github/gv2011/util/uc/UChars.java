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
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.function.IntUnaryOperator;

public final class UChars {

  private static final UStrFactory FACTORY = new UStrFactoryImp();

  public static UStrFactory uStrFactory(){
    return FACTORY;
  }

  public static UStr collect(final int size, final IntUnaryOperator valueForIndex) {
    return uStrFactory().collect(size, valueForIndex);
  }

  public static UChar uChar(final int codePoint) {
    return uStrFactory().uChar(codePoint);
  }

  public static UStr toUStr(final int[] codepoints) {
    return collect(codepoints.length, i->codepoints[i]);
  }

  public static final String toString(final int[] codepoints) {
    return Arrays.stream(codepoints).mapToObj(UChars::toString).collect(joining());
  }

  public static final String toString(final int codepoint) {
    return uChar(codepoint).toString();
  }

  public static boolean isSurrogate(final int codepoint) {
    return UCharImp.isSurrogate(codepoint);
  }

  public static int toCodepoint(final String str) {
    return UCharImp.toCodepoint(str);
  }

  public static UStr uStr(final String str) {
    return FACTORY.uStr(str);
  }

}
