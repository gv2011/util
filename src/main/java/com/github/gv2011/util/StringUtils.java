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
  
  public static String alignRight(final CharSequence str, int size, char fill) {
	char[] chars = new char[size];
	int fillSize = size-str.length();
	if(fillSize<0) throw new IllegalArgumentException("Does not fit.");
	for(int i=0; i<fillSize; i++) chars[i]=fill;
	for(int i=0; i<str.length(); i++) chars[fillSize+i]=str.charAt(i);
	return String.copyValueOf(chars);
  }


}
