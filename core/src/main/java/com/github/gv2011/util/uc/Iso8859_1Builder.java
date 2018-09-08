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
import static com.github.gv2011.util.ex.Exceptions.call;

import java.io.ByteArrayOutputStream;

final class Iso8859_1Builder extends DelegateBuilder{

  private ByteArrayOutputStream bos = new ByteArrayOutputStream();

  @Override
  UStr build() {
    return new Iso8859_1String(bos.toByteArray());
  }

  @Override
  void append(final int codePoint) {
    assert fits(codePoint);
    bos.write(codePoint);
  }

  @Override
  boolean fits(final int codePoint) {
    return codePoint<=UChar.MAX_ISO;
  }

  @Override
  void set(final int index, final int codePoint) {
    assert fits(codePoint);
    final byte[] bytes = bos.toByteArray();
    bytes[index] = (byte) codePoint;
    bos = new ByteArrayOutputStream();
    call(()->bos.write(bytes));
  }

}