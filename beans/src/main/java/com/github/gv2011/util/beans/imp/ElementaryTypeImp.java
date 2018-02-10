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
import java.util.Optional;

import com.github.gv2011.util.beans.ElementaryTypeHandler;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;

final class ElementaryTypeImp<E> extends AbstractType<E>{

  private final ElementaryTypeHandler<E> handler;

  ElementaryTypeImp(final JsonFactory jf, final Class<E> clazz, final ElementaryTypeHandler<E> handler) {
    super(jf, clazz);
    this.handler = handler;
  }

  @Override
  public E parse(final JsonNode json) {
    return handler.fromJson(json);
  }

  @Override
  public JsonNode toJson(final E object) {
    return handler.toJson(object, jf);
  }

  @Override
  public boolean isDefault(final E obj) {
    return handler.defaultValue().map(d->d.equals(obj)).orElse(false);
  }

  @Override
  public Optional<E> getDefault() {
    return handler.defaultValue();
  }

  JsonNodeType jsonNodeType() {
    return handler.jsonNodeType();
  }

}
