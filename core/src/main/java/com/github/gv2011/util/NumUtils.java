package com.github.gv2011.util;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

public final class NumUtils {

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

}
