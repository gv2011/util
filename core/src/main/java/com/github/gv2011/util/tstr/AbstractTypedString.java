package com.github.gv2011.util.tstr;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.uc.UChar;
import com.github.gv2011.util.uc.UStr;

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

public abstract class AbstractTypedString<T extends TypedString<T>>
implements TypedString<T>{

  @Override
  public final String toString() {
    return toStr().toString();
  }

  @Override
  public int hashCode(){
    return TypedString.hashCode(this);
  }

  @Override
  public final boolean equals(final Object obj) {
    return TypedString.equal(this, obj);
  }

  @Override
  public final int compareTo(final TypedString<?> o) {
    return COMPARATOR.compare(this, o);
  }

  @Override
  public UChar get(final int index) {
    return toStr().get(index);
  }

  @Override
  public UStr addElement(final UChar other) {
    return toStr().addElement(other);
  }

  @Override
  public UStr subList(final int fromIndex, final int toIndex) {
    return toStr().subList(fromIndex, toIndex);
  }

  @Override
  public UStr subtract(final Collection<?> other) {
    return toStr().subtract(other);
  }

  @Override
  public UStr join(final Collection<? extends UChar> other) {
    return toStr().join(other);
  }

  @Override
  public UStr asList() {
    return toStr().asList();
  }

  @Override
  public UStr tail() {
    return toStr().tail();
  }

  @Override
  public IList<UChar> reversed() {
    return toStr().reversed();
  }

  @Override
  public ISet<UChar> intersection(final Collection<?> other) {
    return toStr().intersection(other);
  }

  @Override
  public boolean contains(final Object o) {
    return toStr().contains(o);
  }

  @Override
  public Iterator<UChar> iterator() {
    return toStr().iterator();
  }

  @Override
  public boolean containsAll(final Collection<?> c) {
    return toStr().containsAll(c);
  }

  @Override
  public ListIterator<UChar> listIterator() {
    return toStr().listIterator();
  }

  @Override
  public ListIterator<UChar> listIterator(final int index) {
    return toStr().listIterator(index);
  }

  @Override
  public int getCodePoint(final int index) {
    return toStr().getCodePoint(index);
  }

  @Override
  public int size() {
    return toStr().size();
  }

}
