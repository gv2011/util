package com.github.gv2011.util.beans.imp;

import com.github.gv2011.util.icol.Opt;
/*-
 * #%L
 * util-beans
 * %%
 * Copyright (C) 2017 - 2018 Vinz (https://github.com/gv2011)
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
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;

public final class ForeignType<T> extends TypeSupport<T>{

  private final JsonFactory jf;

  ForeignType(final JsonFactory jf, final Class<T> clazz) {
    super(clazz);
    this.jf = jf;
  }

  @Override
  public T parse(final JsonNode json) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonNode toJson(final T object) {
    throw new UnsupportedOperationException();
  }

  @Override
  JsonFactory jf() {
    return jf;
  }

  @Override
  protected boolean hasStringForm() {
    return false;
  }

  @Override
  public boolean isForeignType() {
    return true;
  }

  @Override
  protected final Opt<BeanTypeSupport<T>> asBeanType(){
    return Opt.empty();
  }
}
