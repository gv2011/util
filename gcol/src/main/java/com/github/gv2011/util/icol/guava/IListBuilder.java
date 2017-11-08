package com.github.gv2011.util.icol.guava;

/*-
 * %---license-start---
 * The MIT License (MIT)
 * %
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %
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
 * %---license-end---
 */
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.github.gv2011.util.icol.ICollection;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IList.Builder;
import com.google.common.collect.ImmutableList;

final class IListBuilder<E> implements IList.Builder<E> {

  private final List<E> list = Collections.synchronizedList(new ArrayList<>());

  @Override
  public Builder<E> add(final E element) {
    list.add(notNull(element));
    return this;
  }

  @Override
  public boolean tryAdd(final E element) {
    return list.add(notNull(element));
   }

  @Override
  public <F extends E> Builder<E> addAll(final Collection<F> elements) {
    verify(elements.stream().allMatch(e->e!=null));
    list.addAll(elements);
    return this;
  }

  @Override
  public <F extends E> Builder<E> addAll(final ICollection<F> elements) {
    list.addAll(elements);
    return this;
  }

  @Override
  public <F extends E> Builder<E> tryAddAll(final ICollection<F> elements) {
    return addAll(elements);
  }

  @Override
  public <F extends E> Builder<E> tryAddAll(final Collection<F> elements) {
    return addAll(elements);
  }

  @Override
  public IList<E> build() {
    synchronized(list){
      return new IListWrapper<>(ImmutableList.copyOf(list));
    }
  }

}
