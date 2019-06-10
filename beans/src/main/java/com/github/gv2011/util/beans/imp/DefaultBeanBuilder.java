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
import java.lang.reflect.Proxy;
import java.util.function.UnaryOperator;

import com.github.gv2011.util.icol.ISortedMap;

final class DefaultBeanBuilder<B> extends BeanBuilderSupport<B>{

  private final BeanTypeSupport<B> beanType;

  DefaultBeanBuilder(
    final DefaultBeanType<B> beanType,
    final UnaryOperator<B> resultWrapper,
    final UnaryOperator<B> validator
  ) {
    super(resultWrapper, validator);
    this.beanType = beanType;
  }

  DefaultBeanBuilder(
    final PolymorphicBeanType<B> beanType,
    final UnaryOperator<B> resultWrapper,
    final UnaryOperator<B> validator
  ) {
    super(resultWrapper, validator);
    this.beanType = beanType;
  }

  @Override
  protected BeanTypeSupport<B> beanType() {
    return beanType;
  }

  @Override
  protected B create(final ISortedMap<String, Object> imap) {
    return beanType.clazz.cast(Proxy.newProxyInstance(
        beanType.clazz.getClassLoader(),
        new Class<?>[] {beanType.clazz},
        new DefaultInvocationHandler<>(beanType, imap)
    ));
  }

}
