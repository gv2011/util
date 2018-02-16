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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.github.gv2011.util.icol.CollectionBuilder;
import com.github.gv2011.util.icol.ICollection;
import com.github.gv2011.util.icol.IList;

abstract class AbstractIListBuilder<C extends IList<E>,E,B extends CollectionBuilder<C,E,B>>
implements CollectionBuilder<C,E,B> {

  final List<E> list = Collections.synchronizedList(new ArrayList<>());

  abstract B self();

  @Override
  public B add(final E element) {
    list.add(notNull(element));
    return self();
  }

  @Override
  public boolean tryAdd(final E element) {
    return list.add(notNull(element));
   }

  @Override
  public <F extends E> B addAll(final Collection<F> elements) {
    verify(elements.stream().allMatch(e->e!=null));
    list.addAll(elements);
    return self();
  }

  @Override
  public <F extends E> B addAll(final ICollection<F> elements) {
    list.addAll(elements);
    return self();
  }

  @Override
  public <F extends E> B tryAddAll(final ICollection<F> elements) {
    return addAll(elements);
  }

  @Override
  public <F extends E> B tryAddAll(final Collection<F> elements) {
    return addAll(elements);
  }

}
