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

public enum CharacterType {
  UNASSIGNED,
  UPPERCASE_LETTER,
  LOWERCASE_LETTER,
  TITLECASE_LETTER,
  MODIFIER_LETTER,
  OTHER_LETTER,
  NON_SPACING_MARK,
  ENCLOSING_MARK,
  COMBINING_SPACING_MARK,
  DECIMAL_DIGIT_NUMBER,
  LETTER_NUMBER,
  OTHER_NUMBER,
  SPACE_SEPARATOR,
  LINE_SEPARATOR,
  PARAGRAPH_SEPARATOR,
  CONTROL,
  FORMAT,
  UNKNOWN,
  PRIVATE_USE,
  SURROGATE,
  DASH_PUNCTUATION,
  START_PUNCTUATION,
  END_PUNCTUATION,
  CONNECTOR_PUNCTUATION,
  OTHER_PUNCTUATION,
  MATH_SYMBOL,
  CURRENCY_SYMBOL,
  MODIFIER_SYMBOL,
  OTHER_SYMBOL,
  INITIAL_QUOTE_PUNCTUATION,
  FINAL_QUOTE_PUNCTUATION;

  public static final CharacterType forInt(final int i){
    verify(i>=0 && i<=FINAL_QUOTE_PUNCTUATION.ordinal() && i!=UNKNOWN.ordinal());
    return CharacterType.values()[i];
  }
}
