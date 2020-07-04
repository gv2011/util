package com.github.gv2011.util.uc;

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

import com.github.gv2011.util.CharacterType;
import com.github.gv2011.util.icol.Opt;

abstract class UCharImp implements UChar{

  static final int MAX_ISO = 0x100-1;
  static final int MAX_BMP = 0x10000-1;


  private static final IsoChar[] TABLE;
  static final UCharImp MIN_VALUE;
  static final UCharImp MAX_VALUE;

  static final UCharImp LINE_FEED;
  static final UCharImp QUOTATION_MARK;
  static final UCharImp REVERSE_SOLIDUS;
  static final UCharImp REPLACEMENT_CHARACTER;

  static{
    TABLE = new IsoChar[MAX_ISO+1];
    for(int cp=0; cp<=MAX_ISO; cp++) TABLE[cp] = new IsoChar(cp);
    MIN_VALUE = TABLE[0];
    MAX_VALUE = uChar(Character.MAX_CODE_POINT);
    LINE_FEED = TABLE['\n'];
    QUOTATION_MARK = TABLE['\"'];
    REVERSE_SOLIDUS = TABLE['\\'];
    REPLACEMENT_CHARACTER = uChar(0xfffd);
  }

  static final UCharImp uChar(final String character){
    return uChar(toCodepoint(character));
  }

  static final UCharImp uChar(final int codePoint){
    verify(
      codePoint,
      Character::isValidCodePoint,
      cp->format("Integer {} is not a valid code point.", Integer.toHexString(cp))
    );
    verify(
      codePoint,
      cp -> !isSurrogate(cp),
      cp->format("Codepoint {} is a surrogate.", Character.getName(cp))
    );
    final UCharImp result;
    if(codePoint<=MAX_ISO) result = TABLE[codePoint];
    else if(codePoint<=MAX_BMP) result = new BmpChar((char) codePoint);
    else result = new HighChar(codePoint);
    return result;
  }


  static int verifyNotSurrogate(final int codePoint) {
    verify(
      !isSurrogate(codePoint),
      ()->format("Code point {} is a surrogate.", Integer.toHexString(codePoint))
    );
    return codePoint;
  }


  static final String toString(final int codePoint){
    if(codePoint < Character.MAX_VALUE){
      verify(codePoint, cp->cp>=0 && !isSurrogate(cp));
      return Character.toString((char) codePoint);
    }else{
      return new String(Character.toChars(codePoint));
    }
  }

  static final boolean isSurrogate(final int codePoint){
    return
      (codePoint < Character.MIN_VALUE || codePoint > Character.MAX_VALUE)
      ? false
      : Character.isSurrogate((char) codePoint)
    ;
  }

  static final boolean isSurrogate(final char ch){
    return Character.isSurrogate(ch);
  }

  static final int toCodepoint(final String str){
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

  @Override
  public final String name(){
    return Opt.ofNullable(Character.getName(codePoint())).orElse("");
  }

  @Override
  public abstract int codePoint();

  @Override
  public String toString(){
    return toString(codePoint());
  }

  @Override
  public final CharacterType type(){
    return CharacterType.forInt(Character.getType(codePoint()));
  }

  @Override
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

  @Override
  public final boolean ascii(){
    return codePoint()<128;
  }

  @Override
  public final boolean inBaseSet(){
    return
      ascii() &&
      !type().equals(CharacterType.CONTROL) &&
      !type().equals(CharacterType.MODIFIER_SYMBOL) &&
      !equals(REVERSE_SOLIDUS) &&
      !equals(QUOTATION_MARK)
    ;
  }

  @Override
  public final String printable(){
    if(inBaseSet()) return toString();
    else return "\\"+Integer.toHexString(codePoint())+"\\";
  }

  @Override
  public final boolean equals(final Object other) {
    if(this==other) return true;
    else if(other instanceof UCharImp) return codePoint()==((UCharImp)other).codePoint();
    else return false;
  }

  private static final class IsoChar extends UCharImp{
    private final byte c;
    private IsoChar(final int codePoint) {
      assert codePoint>=0 && codePoint<=MAX_ISO;
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
    @Override
    public boolean isIso8859_1Character() {
      return true;
    }
    @Override
    public boolean isBmpCharacter() {
      return true;
    }
  }

  private static final class BmpChar extends UCharImp{
    private final char codePoint;
    private BmpChar(final char codePoint) {
      assert codePoint>MAX_ISO && !Character.isSurrogate(codePoint);
      this.codePoint = codePoint;
    }
    @Override
    public int codePoint() {
      return codePoint;
    }
    @Override
    public final String toString(){
      return Character.toString(codePoint);
    }
    @Override
    public boolean isIso8859_1Character() {
      return false;
    }
    @Override
    public boolean isBmpCharacter() {
      return true;
    }
  }

  private static final class HighChar extends UCharImp{
    private final int codePoint;
    private HighChar(final int codePoint) {
      assert codePoint>MAX_BMP && codePoint <= Character.MAX_CODE_POINT;
      this.codePoint = codePoint;
    }
    @Override
    public int codePoint() {
      return codePoint;
    }
    @Override
    public boolean isIso8859_1Character() {
      return false;
    }
    @Override
    public boolean isBmpCharacter() {
      return false;
    }
  }

  public static boolean isCharacter(final int codePoint) {
    return codePoint < Character.MIN_VALUE
      ? false
      :(codePoint <= Character.MAX_VALUE
        ? !Character.isSurrogate((char) codePoint)
        : codePoint <= Character.MAX_CODE_POINT
      )
    ;
  }

}
