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
import java.util.Set;
import java.util.function.Supplier;

import com.github.gv2011.util.icol.AbstractCollectionCollector;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedSet;

final class ISortedSetCollector<T extends Comparable<?>>
extends AbstractCollectionCollector<ISortedSet<T>, T, ISortedSet.Builder<T>>{

  private static final ISet<Characteristics> CHARACTERISTICS =
    new ISetBuilder<Characteristics>().add(Characteristics.CONCURRENT).add(Characteristics.UNORDERED).build()
  ;

  ISortedSetCollector() {super(TRY_ADD);}

  @Override
  public Set<Characteristics> characteristics() {
    return CHARACTERISTICS;
  }

  @Override
  public Supplier<ISortedSet.Builder<T>> supplier() {
    return ISortedSetBuilder::new;
  }


}
