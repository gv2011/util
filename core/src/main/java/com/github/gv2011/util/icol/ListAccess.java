package com.github.gv2011.util.icol;

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
import static com.github.gv2011.util.CollectionUtils.intRange;

import java.util.stream.IntStream;

import com.github.gv2011.util.XStream;

public interface ListAccess<E> {

  int size();

  E get(int index);

  IList<E> subList(int fromIndex, int toIndex);

  ISortedMap<Integer,E> asMap();

  default IList<E> tail(){
    return subList(1, size());
  }

  /**
   * @return index of first occurence of <ode>obj</code> or -1 if it is not in the collection.
   */
  default int indexOf(final Object obj){
    return IntStream.range(0,size()).filter(i->get(i).equals(obj)).findFirst().orElse(-1);
  }

  /**
   * @return index of first occurence of <ode>element</code> or -1 if it is not in the collection.
   */
  default int indexOfElement(final E element){
    return indexOf(element);
  }

  /**
   * @return index of last occurence of <ode>obj</code> or -1 if it is not in the list.
   */
  default int lastIndexOf(final Object obj) {
    final int size = size();
    return intRange(size-1,0).filter(i->get(i).equals(obj)).findFirst().orElse(-1);
  }

  /**
   * @return index of last occurence of <ode>element</code> or -1 if it is not in the list.
   */
  default int lastIndexOfElement(final E element) {
    return lastIndexOf(element);
  }

  XStream<E> stream();

}
