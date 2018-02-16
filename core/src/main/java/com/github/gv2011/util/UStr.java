package com.github.gv2011.util;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
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




import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.IntStream;

import com.github.gv2011.util.icol.AbstractIList;

public abstract class UStr extends AbstractIList<UChar> implements CharSequence{

  public static final UStr EMPTY = new IsoStr(new byte[0]);

  public static final UStr uStr(final UChar c){
    final int cp = c.codePoint();
    if(cp<=UChar.MAX_ISO) return new IsoStr(new byte[]{(byte) cp});
    else if(cp<=UChar.MAX_BMP) return new BmpStr(new char[]{(char) cp});
    else return new Utf8Str(c.toString().getBytes(UTF_8));
  }

  public static final UStr uStr(final String str){
    if(str.isEmpty()) return EMPTY;
    else if(
      str.codePoints()
      .map(UChar::verifyNotSurrogate)
      .allMatch(cp->cp<=UChar.MAX_ISO)
    ){
      return new IsoStr(str.getBytes(StandardCharsets.ISO_8859_1));
    }
    else if(str.codePoints().allMatch(cp->cp<=UChar.MAX_BMP)){
      return new BmpStr(str.toCharArray());
    }
    else{
      return new Utf8Str(str.getBytes(UTF_8));
    }
  }

  public static final UStr uStr(final int[] codepoints){
    if(codepoints.length==0) return EMPTY;
    else if(Arrays.stream(codepoints).allMatch(cp->cp<=UChar.MAX_ISO)){
      final byte[] bytes = new byte[codepoints.length];
      for(int i=0; i<codepoints.length; i++) bytes[i] = (byte) codepoints[i];
      return new IsoStr(bytes);
    }
    else if(Arrays.stream(codepoints).allMatch(cp->cp<=UChar.MAX_BMP)){
      final char[] chars = new char[codepoints.length];
      for(int i=0; i<codepoints.length; i++) chars[i] = (char) codepoints[i];
      return new BmpStr(chars);
    }
    else{
      return new Utf8Str(codepoints);
    }
  }

  public static final int[] toCodePoints(final String s){
    return s.codePoints().toArray();
  }

  public static final String toString(final int[] codepoints){
    return Arrays.stream(codepoints).mapToObj(UChar::toString).collect(joining());
  }

  public abstract int getCodePoint(final int index);

  @Override
  public final UChar get(final int index) {
    return UChar.uChar(getCodePoint(index));
  }

  @Override
  public final CharSequence subSequence(final int start, final int end) {
    return toString().subSequence(start, end);
  }

  @Override
  public final IntStream codePoints() {
    return IntStream.range(0, size()).map(this::getCodePoint);
  }

  private static final class IsoStr extends UStr{
    private final byte[] chars;
    private IsoStr(final byte[] chars) {
      this.chars = chars;
    }
    @Override
    public int size() {
      return chars.length;
    }
    @Override
    public int getCodePoint(final int index) {
      return Byte.toUnsignedInt(chars[index]);
    }
    @Override
    public String toString() {
      return new String(chars, StandardCharsets.ISO_8859_1);
    }
    @Override
    public int length() {
      return chars.length;
    }
    @Override
    public char charAt(final int index) {
      return (char) Byte.toUnsignedInt(chars[index]);
    }
    @Override
    public IntStream chars() {
      return IntStream.range(0, size()).map(this::getCodePoint);
    }
  }

  private static final class BmpStr extends UStr{
    private final char[] chars;
    private BmpStr(final char[] chars) {
      this.chars = chars;
    }
    @Override
    public int size() {
      return chars.length;
    }
    @Override
    public int getCodePoint(final int index) {
      return chars[index];
    }
    @Override
    public String toString() {
      return new String(chars);
    }
    @Override
    public int length() {
      return chars.length;
    }
    @Override
    public char charAt(final int index) {
      return chars[index];
    }
  }

  private static final class Utf8Str extends UStr{
    private final CachedConstant<String> str;
    private final Constant<int[]> codePoints;
    private Utf8Str(final byte[] utf8) {
      str = Constants.softRefConstant(()->new String(utf8, UTF_8));
      codePoints = Constants.softRefConstant(()->UStr.toCodePoints(toString()));
    }
    private Utf8Str(final int[] codepoints) {
      final String str = toString(codepoints);
      final byte[] utf8 = str.getBytes(UTF_8);
      this.str = Constants.softRefConstant(()->new String(utf8, UTF_8));
      this.str.set(str);
      codePoints = Constants.softRefConstant(()->UStr.toCodePoints(toString()));
    }
    @Override
    public int size() {
      return codePoints.get().length;
    }
    @Override
    public int getCodePoint(final int index) {
      return codePoints.get()[index];
    }
    @Override
    public String toString() {
      return str.get();
    }
    @Override
    public int length() {
      return toString().length();
    }
    @Override
    public char charAt(final int index) {
      return toString().charAt(index);
    }
  }

}
