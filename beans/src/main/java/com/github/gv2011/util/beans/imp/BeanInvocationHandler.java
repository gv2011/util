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

import static com.github.gv2011.util.Verify.notNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.icol.ISortedMap;

final class BeanInvocationHandler<B> implements InvocationHandler {

    private final DefaultBeanType<B> beanType;
    final ISortedMap<String, Object> values;

    private @Nullable Integer hashCode = null;

    BeanInvocationHandler(final DefaultBeanType<B> beanType, final ISortedMap<String, Object> values) {
      this.beanType = beanType;
      this.values = values;
    }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    Object result;
    final String name = method.getName();
    if(method.getParameterCount()==0) {
      if(name.equals("hashCode")) result = getHashCode();
      else if(name.equals("toString")) result = beanType.clazz.getSimpleName()+values;
      else result = getValue(name);
    }
    else if(name.equals("equals") && method.getParameterCount()==1){
      result = handleEquals(proxy, notNull(args[0]));
    }
    else throw new UnsupportedOperationException(method.toString());
    return result;
  }

  private int getHashCode() {
    if(hashCode==null) hashCode = beanType.hashCodeFromValues(values);
    return hashCode.intValue();
  }

  private Object getValue(final String property) {
    assert beanType.properties().containsKey(property);
    return values.tryGet(property).orElseGet(()->beanType.properties().get(property).defaultValue().get());
  }

  private boolean handleEquals(final Object proxy, final Object other){
    boolean result;
    if(proxy==other) result = true;
    else if(!beanType.clazz.isInstance(other)) result = false;
    else{
      final B otherBean = beanType.clazz.cast(other);
      if(Proxy.isProxyClass(other.getClass())) {
        final InvocationHandler oih = Proxy.getInvocationHandler(other);
        if(oih.getClass().equals(BeanInvocationHandler.class)) {
          final BeanInvocationHandler<?> obih = (BeanInvocationHandler<?>)oih;
          assert obih.beanType.equals(beanType);
          if(getHashCode()!=other.hashCode()){
            result = false;
            assert result == equals1(otherBean);
          }
          else{
            result = obih.values.equals(values);
            assert result == equals1(otherBean);
          }
        }
        else result = equals1(otherBean);
      }
      else result = equals1(otherBean);
    }
    return result;
  }

  private boolean equals1(final B other) {
    return beanType.properties().values().stream()
      .allMatch(p->getValue(p.name()).equals(p.getValue(other)))
    ;
  }

}
