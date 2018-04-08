package com.github.gv2011.util.beans.imp;

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
import com.github.gv2011.util.beans.ElementaryTypeHandler;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;

abstract class AbstractElementaryType<E> extends TypeSupport<E>{

  private final JsonFactory jf;

  AbstractElementaryType(final JsonFactory jf, final Class<E> clazz) {
    super(clazz);
    this.jf = jf;
  }

  @Override
  final JsonFactory jf() {
    return jf;
  }

  abstract ElementaryTypeHandler<E> handler();

  @Override
  public final E parse(final JsonNode json) {
    return handler().fromJson(json);
  }

  @Override
  public final JsonNode toJson(final E object) {
    return handler().toJson(object, jf());
  }

  @Override
  public final boolean isDefault(final E obj) {
    return handler().defaultValue().map(d->d.equals(obj)).orElse(false);
  }

  @Override
  public final Opt<E> getDefault() {
    return handler().defaultValue();
  }

  final JsonNodeType jsonNodeType() {
    return handler().jsonNodeType();
  }

  @Override
  public E cast(final Object object) {
    return handler().cast(clazz, object);
  }

}
