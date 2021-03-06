package com.github.gv2011.util.beans.cglib;

import java.util.function.UnaryOperator;

/*-
 * #%L
 * util-beans-cglib
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
import com.github.gv2011.util.beans.imp.BeanBuilderSupport;
import com.github.gv2011.util.beans.imp.BeanTypeSupport;
import com.github.gv2011.util.icol.ISortedMap;

import net.sf.cglib.proxy.Enhancer;

final class CglibBeanBuilder<B> extends BeanBuilderSupport<B>{

  private final CglibBeanType<B> beanType;

  CglibBeanBuilder(
    final CglibBeanType<B> beanType,
    final UnaryOperator<B> resultWrapper,
    final UnaryOperator<B> validator
  ) {
    super(resultWrapper, validator);
    this.beanType = beanType;
  }

  @Override
  protected B create(final ISortedMap<String, Object> imap) {
    final Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(beanType.clazz);
    enhancer.setCallback(new CglibBeanInvocationHandler<>(beanType, imap));
    return beanType.clazz.cast(enhancer.create());
  }

  @Override
  protected BeanTypeSupport<B> beanType() {
    return beanType;
  }

}
