package com.github.gv2011.util.tstr;

import java.util.Comparator;

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
public interface TypedString<T extends TypedString<T>> extends CharSequence, Comparable<TypedString<?>>{

  public static <T extends TypedString<T>> T create(final Class<T> clazz, final String value) {
    return TypedStringInvocationHandler.create(clazz, value);
  }

  T self();

  Class<T> clazz();

  default boolean isEmpty() {
      return canonical().isEmpty();
  }

  default String canonical() {
    return toString();
  }

  default int compareWithOtherOfSameType(final T o) {
    return canonical().compareTo(o.canonical());
  }

  @Override
  default int compareTo(final TypedString<?> o) {
    return COMPARATOR.compare(this, o);
  }

  @Override
  default int length() {
    return canonical().length();
  }

  @Override
  default char charAt(final int index) {
    return canonical().charAt(index);
  }

  @Override
  default CharSequence subSequence(final int start, final int end) {
    return canonical().subSequence(start, end);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static final Comparator<TypedString<?>> COMPARATOR = (s1,s2)->{
    int result;
    if(s1==s2) result = 0;
    else {
      result = s1.clazz().getName().compareTo(s2.clazz().getName());
      if(result==0) {
        result = ((TypedString)s1).compareWithOtherOfSameType(((TypedString)s2));
      }
    }
    return result;
  };


}
