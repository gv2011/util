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




import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.format;

import java.lang.Character.UnicodeScript;
import java.util.Optional;
import java.util.stream.IntStream;

public abstract class UChar implements Comparable<UChar>{

  static enum Type{ISO,BMP,UNI;
    static final Type type(final int codePoint){
      if(codePoint<=MAX_ISO) return ISO;
      else if(codePoint<=MAX_BMP) return BMP;
      else return UNI;
    }
  };

  static final int MAX_ISO = 0x100-1;
  static final int MAX_BMP = 0x10000-1;


  private static final IsoChar[] TABLE;
  public static final UChar MIN_VALUE;
  public static final UChar MAX_VALUE;

  public static final UChar LINE_FEED;
  public static final UChar QUOTATION_MARK;
  public static final UChar REVERSE_SOLIDUS;
  public static final UChar REPLACEMENT_CHARACTER;

  static{
    TABLE = new IsoChar[MAX_ISO+1];
    IntStream.range(0, TABLE.length).forEach(i->TABLE[i] = new IsoChar(i));
    MIN_VALUE = TABLE[0];
    MAX_VALUE = uChar(Character.MAX_CODE_POINT);
    LINE_FEED = TABLE['\n'];
    QUOTATION_MARK = TABLE['\"'];
    REVERSE_SOLIDUS = TABLE['\\'];
    REPLACEMENT_CHARACTER = uChar(0xfffd);
  }

  public static final UChar uChar(final String character){
    return uChar(toCodepoint(character));
  }

  public static final UChar uChar(final int codePoint){
    verify(Character.isValidCodePoint(codePoint));
    UChar result;
    if(codePoint<=MAX_ISO) result = TABLE[codePoint];
    else if(codePoint<=MAX_BMP) result = new CharChar(codePoint);
    result = new IntChar(codePoint);
    verifyNotSurrogate(codePoint);
    return result;
  }


  public static int verifyNotSurrogate(final int codePoint) {
    verify(
      !isSurrogate(codePoint),
      ()->format("Code point {} is a surrogate.", Integer.toHexString(codePoint))
    );
    return codePoint;
  }


  public static final String toString(final int codePoint){
    return new String(Character.toChars(codePoint));
  }

  public static final boolean isSurrogate(final int codePoint){
    return Character.getType(codePoint)==Character.SURROGATE;
  }

  public static final boolean isSurrogate(final char ch){
    return Character.isSurrogate(ch);
  }

  public static final int toCodepoint(final String str){
    int result;
    if(str.length()==1){
      final char ch = str.charAt(0);
      verify(!isSurrogate(ch));
      result = ch;
    }else{
      verify(str.length()==2);
      final char ch = str.charAt(0);
      final char cl = str.charAt(1);
      verify(Character.isSurrogatePair(ch, cl));
      result = Character.toCodePoint(ch, cl);
    }
    return result;
  }

  public final String name(){
    return Optional.ofNullable(Character.getName(codePoint())).orElse("");
  }

  public abstract int codePoint();

  public final CharacterType type(){
    return CharacterType.forInt(Character.getType(codePoint()));
  }

  public final UnicodeScript script(){
    return UnicodeScript.of(codePoint());
  }

  @Override
  public final int compareTo(final UChar other) {
    return codePoint()-other.codePoint();
  }

  @Override
  public final int hashCode() {
    return codePoint();
  }

  public final boolean ascii(){
    return codePoint()<128;
  }

  public final boolean inBaseSet(){
    return
      ascii() &&
      !type().equals(CharacterType.CONTROL) &&
      !type().equals(CharacterType.MODIFIER_SYMBOL) &&
      !equals(REVERSE_SOLIDUS) &&
      !equals(QUOTATION_MARK)
    ;
  }

  public final String printable(){
    if(inBaseSet()) return toString();
    else return "\\"+Integer.toHexString(codePoint())+"\\";
  }

  @Override
  public final boolean equals(final Object other) {
    if(this==other) return true;
    else if(other instanceof UChar) return codePoint()==((UChar)other).codePoint();
    else return false;
  }

  private static final class IsoChar extends UChar{
    private final byte c;
    private IsoChar(final int codePoint) {
      c = (byte)codePoint;
    }
    @Override
    public int codePoint() {
      return Byte.toUnsignedInt(c);
    }
    @Override
    public final String toString(){
      return Character.toString((char)Byte.toUnsignedInt(c));
    }
  }

  private static final class IntChar extends UChar{
    private final int codePoint;
    private IntChar(final int codePoint) {
      this.codePoint = codePoint;
    }
    @Override
    public int codePoint() {
      return codePoint;
    }
    @Override
    public final String toString(){
      return new String(Character.toChars(codePoint));
    }
  }

  private static final class CharChar extends UChar{
    private final char c;
    private CharChar(final int c) {
      this.c = (char)c;
    }
    @Override
    public int codePoint() {
      return c;
    }
    @Override
    public final String toString(){
      return Character.toString(c);
    }
  }

}
