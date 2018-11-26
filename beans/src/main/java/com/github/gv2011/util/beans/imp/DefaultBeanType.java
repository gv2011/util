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

import static com.github.gv2011.util.Verify.verify;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.github.gv2011.util.beans.AnnotationHandler;
import com.github.gv2011.util.beans.ExtendedBeanBuilder;
import com.github.gv2011.util.json.JsonFactory;


public class DefaultBeanType<T> extends BeanTypeSupport<T>{

  @SuppressWarnings("unused")
  private static final Logger LOG = getLogger(DefaultBeanType.class);

  DefaultBeanType(
    final Class<T> beanClass,
    final JsonFactory jf,
    final AnnotationHandler annotationHandler,
    final BeanFactory beanFactory
  ) {
    super(beanClass, jf, annotationHandler, beanFactory);
    verify(beanClass.isInterface(), beanClass::toString);
  }

  @Override
  public ExtendedBeanBuilder<T> createBuilder() {
    return new DefaultBeanBuilder<>(this, resultWrapper, validator);
  }

  @Override
  public final boolean isAbstract() {
    return false;
  }

}
