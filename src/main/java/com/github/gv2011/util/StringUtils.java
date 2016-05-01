package com.github.gv2011.util;

public final class StringUtils {

  public static String removeWhitespace(final String s){
    return s.replaceAll("\\s+", "");
  }

}
