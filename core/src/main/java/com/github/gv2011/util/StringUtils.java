package com.github.gv2011.util;

import static com.github.gv2011.util.CollectionUtils.toSortedSet;
import static com.github.gv2011.util.FileUtils.getPath;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.run;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.regex.Pattern;

public final class StringUtils {

  public static String removeWhitespace(final String s) {
    return s.replaceAll("\\s+", "");
  }

  public static String removeTail(final String s, final String tail) {
    if(!s.endsWith(tail)) throw new IllegalArgumentException(format("{} does not end with {}.", s, tail));
    return s.substring(0, s.length()-tail.length());
  }

  public static String removePrefix(final String s, final String prefix) {
    if(!s.startsWith(prefix)) throw new IllegalArgumentException(
      format("{} does not start with {}.", s, prefix)
    );
    return s.substring(prefix.length());
  }

  public static String tryRemoveTail(final String s, final String tail) {
    if(s.endsWith(tail)) return s.substring(0, s.length()-tail.length());
    else return s;
  }

  public static String showSpecial(final String s) {
    final StringBuilder result = new StringBuilder();
    for(int i=0; i<s.length(); i++){
      final char c = s.charAt(i);
      if(c=='\\') result.append("\\\\");
      else if(c>=' ' && c<='~') result.append(c);
      else{
        result.append("\\"+Integer.toHexString(c)+";");
        if(c=='\n') result.append('\n');
      }
    }
    return result.toString();
  }

  public static String multiply(final CharSequence str, final int factor) {
    if (factor < 0) throw new IllegalArgumentException();
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < factor; i++)
      sb.append(str);
    return sb.toString();
  }

  public static String alignRight(final Object str, final int size) {
    return alignRight(str.toString(), size, ' ');
  }

  public static String alignRight(final CharSequence str, final int size, final char fill) {
    final char[] chars = new char[size];
    final int fillSize = size - str.length();
    if (fillSize < 0) throw new IllegalArgumentException("Does not fit.");
    for (int i = 0; i < fillSize; i++)
      chars[i] = fill;
    for (int i = 0; i < str.length(); i++)
      chars[fillSize + i] = str.charAt(i);
    return String.copyValueOf(chars);
  }

  public static SortedSet<String> splitToSet(final String str) {
    return Arrays.stream(str.split(Pattern.quote(",")))
      .map(String::trim)
      .filter(s->!s.isEmpty())
      .collect(toSortedSet())
    ;
  }

  @Deprecated//Moved to FileUtils
  public static String readFile(final String path, final String... morePathElements) {
    return call(()->new String(Files.readAllBytes(getPath(path, morePathElements)), UTF_8));
  }

  @Deprecated//Moved to FileUtils
  public static void writeFile(final String text, final String path, final String... morePathElements) {
    writeFile(text, getPath(path, morePathElements));
  }

  @Deprecated//Moved to FileUtils
  public static void writeFile(final String text, final Path path) {
    run(()->Files.write(path, text.getBytes(UTF_8)));
  }

}
