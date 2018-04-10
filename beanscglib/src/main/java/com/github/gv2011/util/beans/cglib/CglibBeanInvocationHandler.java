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
import static com.github.gv2011.util.ex.Exceptions.wrap;

import java.lang.reflect.Method;

import com.github.gv2011.util.beans.imp.BeanInvocationHandlerSupport;
import com.github.gv2011.util.icol.ISortedMap;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

final class CglibBeanInvocationHandler<B> extends BeanInvocationHandlerSupport<B, MethodProxy> implements MethodInterceptor{

  CglibBeanInvocationHandler(final CglibBeanType<B> beanType, final ISortedMap<String, Object> values) {
    super(beanType, values);
  }

  @Override
  public Object intercept(final Object proxy, final Method method, final Object[] args, final MethodProxy np
  ) throws Throwable {
    return handle(proxy, method, args, np);
  }

  @Override
  protected Object handleOther(final Object proxy, final Method method, final Object[] args, final MethodProxy mp) {
    try {
      return mp.invokeSuper(proxy, args);
    }
    catch (final RuntimeException e) {throw e;}
    catch (final Error e) {throw e;}
    catch (final Throwable e) {throw wrap(e);}
  }

  @Override
  protected String handleToString(final Object proxy, final Method method, final Object[] args, final MethodProxy mp) {
    if(!method.getDeclaringClass().equals(Object.class)){
      try {
        return (String) mp.invokeSuper(proxy, args);
      }
      catch (final RuntimeException e) {throw e;}
      catch (final Error e) {throw e;}
      catch (final Throwable e) {throw wrap(e);}
    }
    else return super.handleToString(proxy, method, args, mp);
  }

}
