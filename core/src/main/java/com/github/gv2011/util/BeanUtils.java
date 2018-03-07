package com.github.gv2011.util;

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

import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.json.JsonUtils.jsonFactory;

import com.github.gv2011.util.beans.BeanBuilder;
import com.github.gv2011.util.beans.TypeRegistry;
import com.github.gv2011.util.json.JsonObject;

public final class BeanUtils {

  private static final Constant<TypeRegistry> TYPE_REGISTRY = ServiceLoaderUtils.lazyServiceLoader(TypeRegistry.class);

  private BeanUtils(){staticClass();}

  public static TypeRegistry typeRegistry(){
      return TYPE_REGISTRY.get();
  }

  public static <B> BeanBuilder<B> beanBuilder(final Class<B> beanClass){
      return typeRegistry().createBuilder(beanClass);
  }

  public static <B> B parse(final Class<B> beanClass, final JsonObject json) {
      return typeRegistry().beanType(beanClass).parse(json);
  }

  public static <B> B parse(final Class<B> beanClass, final String json) {
      return parse(beanClass, jsonFactory().deserialize(json).asObject());
  }
}
