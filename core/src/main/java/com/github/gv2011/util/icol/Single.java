package com.github.gv2011.util.icol;

import java.util.Arrays;
import java.util.Collection;
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
import java.util.function.Function;

import com.github.gv2011.util.ann.Nullable;

public final class Single<E> extends Ref<E>{

  @SuppressWarnings("unchecked")
  public static final <T> Opt<T> ofNullable(final @Nullable T obj){
    return obj==null ? IEmpty.INSTANCE : of(obj);
  }

  public static final <T> Opt<T> of(final T obj){
    return new Single<>(obj);
  }

  private final E element;

  Single(final E element) {
    this.element = element;
  }

  @Override
  public E get() {
    return element;
  }

  @Override
  public <U> Opt<U> map(final Function<? super E, ? extends U> mapper) {
    return new Single<>(mapper.apply(element));
  }

  @SuppressWarnings("unchecked")
  @Override
  public Opt<E> subtract(final Collection<?> other) {
    return other.contains(element) ? this : IEmpty.INSTANCE;
  }

  @Override
  public ISet<E> addElement(final E element) {
    return ICollections.<E>setBuilder().add(this.element).add(element).build();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T[] toArray(final T[] a) {
    T[] result;
    if(a.length==0){
      result = Arrays.copyOf(a, 1);
      result[0] = (T) element;
    }
    else{
      if(a.length>1) a[1] = null;
      a[0] = (T) element;
      result = a;
    }
    return result;
  }

}
