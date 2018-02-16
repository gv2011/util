package com.github.gv2011.util.icol;

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




import java.util.Arrays;
import java.util.Collection;

import com.github.gv2011.util.Builder;

public interface CollectionBuilder<C extends ICollection<E>,E,B extends CollectionBuilder<C,E,B>> extends Builder<C>{

  B add(E element);

  /**
   * @return true, if added
   */
  boolean tryAdd(E element);

  default <F extends E> B addAll(final ICollection<F> elements){return addAll((Collection<F>) elements);}

  <F extends E> B addAll(Collection<F> elements);

  default <F extends E> B addAll(final F[] elements){return addAll(Arrays.asList(elements));}

  default <F extends E> B tryAddAll(final ICollection<F> elements){return tryAddAll((Collection<F>) elements);}

  <F extends E> B tryAddAll(Collection<F> elements);

}
