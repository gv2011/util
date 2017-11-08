package com.github.gv2011.util;

/*-
 * %---license-start---
 * The MIT License (MIT)
 * %
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %
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
 * %---license-end---
 */



import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.math.BigInteger;

public final class NumUtils {

  private static final BigInteger MIN_INT = BigInteger.valueOf(Integer.MIN_VALUE);
  private static final BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);

  private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
  private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

  private NumUtils(){staticClass();}

  public static boolean isOdd(final int n){
    return n%2==1;
  }

  public static String withLeadingZeros(final int i, final int digits){
    final StringBuilder sb = new StringBuilder(Integer.toString(Math.abs(i)));
    verify(sb.length()<=digits);
    while(sb.length()<digits) sb.insert(0, '0');
    if(i<0) sb.insert(0,'-');
    return sb.toString();
  }
  
  public static boolean isInt(final BigInteger i) {
    return i.compareTo(MIN_INT)>=0 && i.compareTo(MAX_INT)<=0;
  }

  public static boolean isLong(final BigInteger i) {
    return i.compareTo(MIN_LONG)>=0 && i.compareTo(MAX_LONG)<=0;
  }

}
