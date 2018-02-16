package com.github.gv2011.util.icol.guava;

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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.github.gv2011.util.Comparison;
import com.github.gv2011.util.icol.Path;

final class PathImp extends IListWrapper<String> implements Path{

  private static final Comparator<Path> COMPARATOR = Comparison.listComparator();

  @SuppressWarnings("unchecked")
  static final Path EMPTY = new PathImp(GuavaIcolFactory.EMPTY_LIST);

  private PathImp(final List<String> delegate) {
    super(delegate);
  }

  @Override
  public int compareTo(final Path o) {
    return COMPARATOR.compare(this, o);
  }

  @Override
  public Path append(final String element) {
    return new PathImp(super.append(element));
  }

  @Override
  public Path appendAll(final Iterable<? extends String> elements) {
    return new PathImp(super.appendAll(elements));
  }

  @Override
  public Optional<Path> parent() {
    return isEmpty()
      ? Optional.empty()
      : Optional.of(size()==1
        ? EMPTY
        : new PathImp(delegate.subList(0, delegate.size()-1))
      )
    ;
  }


}
