package com.github.gv2011.util.gcol;

import com.github.gv2011.util.icol.ICollections;

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

import com.github.gv2011.util.icol.IList;
import com.google.common.collect.ImmutableList;

final class IListBuilder<E> extends AbstractIListBuilder<IList<E>,E,IList.Builder<E>> implements IList.Builder<E> {

  @Override
  IList.Builder<E> self() {
    return this;
  }


  @Override
  public void insert(final int index, final E element) {
    synchronized(list){
      list.add(index, element);
    }
  }


  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public IList<E> build() {
    synchronized(list){
      if(list.isEmpty()) return ICollections.emptyList();
      return new IListWrapper(ImmutableList.copyOf(list));
    }
  }
}
