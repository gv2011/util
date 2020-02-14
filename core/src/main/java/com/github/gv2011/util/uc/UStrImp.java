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
import static com.github.gv2011.util.ex.Exceptions.notYetImplemented;
import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;
import static com.github.gv2011.util.icol.ICollections.toIList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.IntStream;

import com.github.gv2011.util.Comparison;
import com.github.gv2011.util.icol.AbstractIList;
import com.github.gv2011.util.icol.IList;

public abstract class UStrImp extends AbstractIList<UChar> implements UStr {

  public static final UStrImp EMPTY = new Iso8859_15String(new byte[0]);

  private static final Comparator<UStr> COMPARATOR = Comparison.listComparator();

  public static final UStrImp uStr(final UChar c) {
    final int cp = c.codePoint();
    if (cp <= UChar.MAX_ISO) return notYetImplemented();// new IsoStr(new byte[] { (byte) cp });
    else if (cp <= UChar.MAX_BMP) return notYetImplemented();//new BmpStr(new char[] { (char) cp });
    else return notYetImplemented();//new Utf8Str(c.toString().getBytes(UTF_8));
  }

  public static final UStrImp uStr(final String str) {
    return notYetImplemented();
  }

  public static final UStrImp uStr(final int[] codepoints) {
    if (codepoints.length == 0) return EMPTY;
    else if (Arrays.stream(codepoints).allMatch(cp -> cp <= UChar.MAX_ISO)) {
      final byte[] bytes = new byte[codepoints.length];
      for (int i = 0; i < codepoints.length; i++)
        bytes[i] = (byte) codepoints[i];
      return notYetImplemented();//new IsoStr(bytes);
    } else if (Arrays.stream(codepoints).allMatch(cp -> cp <= UChar.MAX_BMP)) {
      final char[] chars = new char[codepoints.length];
      for (int i = 0; i < codepoints.length; i++)
        chars[i] = (char) codepoints[i];
      return notYetImplemented();//new BmpStr(chars);
    } else {
      return notYetImplemented();//new Utf8Str(codepoints.clone());
    }
  }

  public static final int[] toCodePoints(final String s) {
    return s.codePoints().toArray();
  }

  @Override
  public final UChar get(final int index) {
    return UChars.uChar(getCodePoint(index));
  }

  @Override
  public UStr subList(final int fromIndex, final int toIndex) {
    return UChars.collect(toIndex-fromIndex, i->getCodePoint(i+fromIndex));
  }

  @Override
  public UStr subtract(final Collection<?> other) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public UStr join(final Collection<? extends UChar> other) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public UStr asList() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public UStr tail() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }


  public static final UStrImp collect(final IntStream codepoints) {
    return uStr(
        codepoints.collect(
            StringBuilder::new,
            (b, cp) -> b.appendCodePoint(cp),
            (b1, b2) -> b1.append(b2.toString()))
            .toString());
  }

  static UStrImp join(final Iso8859_15String iso8859_1String, final UChar ch) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  static UStr join(final BmpString bmpString, final UChar ch) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    for(int i=0; i<size(); i++) sb.appendCodePoint(getCodePoint(i));
    return sb.toString();
  }

  @Override
  public final UStr toStr(){
    return this;
  }

  @Override
  public final UStr addElement(final UChar ch) {
    return new UStrBuilderImp().append(this).append(ch).build();
  }

  @Override
  public final IList<UChar> reversed() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public int compareTo(final UStr o) {
    if(this==o) return 0;
    else return COMPARATOR.compare(this, o);
  }

  @Override
  public final IList<UStr> split(final UChar tab) {
    final int cp = tab.codePoint();
    final int[] cuts =
      IntStream.concat(
        IntStream.concat(
          IntStream.of(0),
          IntStream.range(0, size()).parallel().filter(i->getCodePoint(i)==cp)
        ),
        IntStream.of(size())
      )
      .toArray()
    ;
    return IntStream.range(0, cuts.length-1).parallel()
      .mapToObj(c->subList(cuts[c], cuts[c+1]))
      .collect(toIList())
    ;
  }

}
