package com.github.gv2011.util.tstr;

import java.util.Comparator;

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




import com.github.gv2011.util.Equal;

public abstract class AbstractTypedString<T extends AbstractTypedString<T>>
implements TypedString<T>{

  public static final int hashCode(final TypedString<?> s) {
    return hashCode(s.clazz(), s.canonical());
  }

  public static final int hashCode(final Class<? extends TypedString<?>> clazz, final String canonical) {
    return clazz.hashCode() * 31 + canonical.hashCode();
  }

  public static final Comparator<TypedString<?>> COMPARATOR = (s1,s2)->{
    int result;
    if(s1==s2) result = 0;
    else {
      result = s1.clazz().getName().compareTo(s2.clazz().getName());
      if(result==0) {
        result = s1.canonical().compareTo(s2.canonical());
      }
    }
    return result;
  };

  public static final boolean equal(final TypedString<?> s, final Object obj) {
    return Equal.equal(s, obj, TypedString.class, o->{
      return s.clazz().equals(o.clazz()) && s.toString().equals(o.toString());
    });
  }



  @Override
  public abstract String toString();

  @Override
  public int hashCode(){
    return hashCode(this);
  }

  @Override
  public final boolean equals(final Object obj) {
    return equal(this, obj);
  }

  @Override
  public final int compareTo(final TypedString<?> o) {
    return COMPARATOR.compare(this, o);
  }

}
