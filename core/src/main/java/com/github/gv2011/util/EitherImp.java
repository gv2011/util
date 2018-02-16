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




import java.util.NoSuchElementException;

final class EitherImp<A,B> implements Either<A,B>{

  public static <A,B> Either<A,B> newThis(final A a) {
    return new EitherImp<>(true, a);
  }

  public static <A,B> Either<A,B> newThat(final B b) {
    return new EitherImp<>(true, b);
  }

  private final boolean isThis;
  private final Object value;

  private EitherImp(final boolean isThis, final Object value) {
    this.isThis = isThis;
    this.value = value;
  }

  @Override
  public boolean isThis() {
    return isThis;
  }

  @Override
  @SuppressWarnings("unchecked")
  public A getThis() {
    if(!isThis) throw new NoSuchElementException();
    return (A) value;
  }

  @Override
  @SuppressWarnings("unchecked")
  public B getThat() {
    if(isThis) throw new NoSuchElementException();
    return (B) value;
  }

  @Override
  public Object get() {
    return value;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if(this==obj) return true;
    else if(!(obj instanceof Either)) return false;
    else return value.equals(((Either<?,?>)obj).get());
  }

  @Override
  public String toString() {
    return value.toString();
  }



}
