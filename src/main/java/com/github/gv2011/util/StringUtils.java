package com.github.gv2011.util;

public final class StringUtils {

  public static String removeWhitespace(final String s){
    return s.replaceAll("\\s+", "");
  }

  public static String multiply(final CharSequence str, final int factor) {
    if(factor<0)throw new IllegalArgumentException();
    final StringBuilder sb = new StringBuilder();
    for(int i=0; i<factor; i++) sb.append(str);
    return sb.toString();
  }

}
