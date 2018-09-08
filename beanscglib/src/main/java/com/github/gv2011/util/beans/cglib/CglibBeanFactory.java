package com.github.gv2011.util.beans.cglib;

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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.function.Function;

import com.github.gv2011.util.beans.AnnotationHandler;
import com.github.gv2011.util.beans.imp.BeanFactory;
import com.github.gv2011.util.beans.imp.DefaultBeanFactory;
import com.github.gv2011.util.beans.imp.DefaultTypeRegistry;
import com.github.gv2011.util.beans.imp.ObjectTypeSupport;
import com.github.gv2011.util.beans.imp.TypeSupport;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonFactory;

final class CglibBeanFactory extends BeanFactory {

  private final BeanFactory interfaceBeanFactory;

  CglibBeanFactory(
    final JsonFactory jf, final AnnotationHandler annotationHandler, final DefaultTypeRegistry registry
  ) {
    super(jf, annotationHandler, registry);
    this.interfaceBeanFactory = new DefaultBeanFactory(jf, annotationHandler, registry);
  }

  @Override
  protected String isProxyable(final Class<?> clazz) {
    return clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) ? "" : "not abstract";
  }

  @Override
  protected boolean isPropertyMethod2(final Method m) {
    return Modifier.isAbstract(m.getModifiers());
  }

  @Override
  public <B> Opt<ObjectTypeSupport<B>> tryCreate(final Class<B> clazz) {
    if(clazz.isInterface()) return interfaceBeanFactory.tryCreate(clazz);
    else return super.tryCreate(clazz);
  }

@Override
  protected <B> ObjectTypeSupport<B> createRegularBeanType(
    final Class<B> clazz,
    final JsonFactory jf,
    final AnnotationHandler annotationHandler,
    final Function<Type,TypeSupport<?>> registry
  ) {
    return new CglibBeanType<>(clazz, jf, annotationHandler, this);
  }

}