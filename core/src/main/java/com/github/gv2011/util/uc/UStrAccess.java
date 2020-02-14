package com.github.gv2011.util.uc;

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
import java.util.Collection;

import com.github.gv2011.util.Immutable;
import com.github.gv2011.util.StringUtils;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.icol.IList;

public interface UStrAccess extends Immutable, IList<UChar>{

  @Override
  UChar get(final int index);

  int getCodePoint(final int index);

  @Override
  UStr addElement(UChar other);

  @Override
  UStr subList(final int fromIndex, final int toIndex);

  @Override
  UStr subtract(Collection<?> other);

  @Override
  UStr join(final Collection<? extends UChar> other);

  @Override
  UStr asList();

  @Override
  UStr tail();

  default TypedBytes utf8(){
    return ByteUtils.asUtf8(toString());
  }

  default boolean isLowerCase(){
    return StringUtils.isLowerCase(toString());
  }

  public IList<UStr> split(UChar tab);


}
