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

import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.format;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.gv2011.util.icol.CollectionBuilder;
import com.github.gv2011.util.icol.ICollection;
import com.github.gv2011.util.icol.ISet;
import com.google.common.collect.ImmutableList;

abstract class AbstractISetBuilder<S extends ISet<E>,E,B extends CollectionBuilder<S,E,B>>
implements CollectionBuilder<S,E,B> {

  protected final Set<E> set = Collections.synchronizedSet(new HashSet<>());

  protected abstract B self();

  @Override
  public final B add(final E element) {
    final boolean added = set.add(notNull(element));
    if(!added) throw new IllegalArgumentException(format("Set already contains {}.", element));
    return self();
  }

  @Override
  public final boolean tryAdd(final E element) {
    return set.add(notNull(element));
  }

  @Override
  public final <F extends E> B addAll(final Collection<F> elements) {
    final ImmutableList<E> copy = ImmutableList.copyOf(elements);
    synchronized(set){
      verify(copy.stream().allMatch(e->e!=null && !set.contains(e)));
      set.addAll(copy);
    }
    return self();
  }

  @Override
  public final <F extends E> B addAll(final ICollection<F> elements) {
    synchronized(set){
      verify(elements.stream().allMatch(e->!set.contains(e)));
      set.addAll(elements);
    }
    return self();
  }



  @Override
  public final <F extends E> B tryAddAll(final ICollection<F> elements) {
    set.addAll(elements);
    return self();
  }

  @Override
  public final <F extends E> B tryAddAll(final Collection<F> elements) {
    final ImmutableList<E> copy = ImmutableList.copyOf(elements);
    verify(copy.stream().allMatch(e->e!=null));
    set.addAll(copy);
    return self();
  }

}
