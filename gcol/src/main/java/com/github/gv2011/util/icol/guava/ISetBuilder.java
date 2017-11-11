package com.github.gv2011.util.icol.guava;

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

import java.util.Comparator;

import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISet.Builder;
import com.github.gv2011.util.icol.ISortedSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

final class ISetBuilder<E> extends AbstractISetBuilder<ISet<E>,E,ISet.Builder<E>> implements ISet.Builder<E>{

  @Override
  protected Builder<E> self() {
    return this;
  }

  @Override
  public ISet<E> build() {
    synchronized(set){
      return new ISetWrapper<>(ImmutableSet.copyOf(set));
    }
  }

  @Override
  public ISortedSet<E> build(final Comparator<? super E> comparator) {
    synchronized(set){
      return new ISortedSetWrapper<>(ImmutableSortedSet.copyOf(comparator, set));
    }
  }

}
