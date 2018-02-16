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
import static com.github.gv2011.util.ex.Exceptions.call;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.github.gv2011.util.icol.ISortedMap;

final class BeanInvocationHandler implements InvocationHandler {

    private final Class<?> beanClass;
    private final ISortedMap<String, Object> values;

    BeanInvocationHandler(final Class<?> beanClass, final ISortedMap<String, Object> values) {
      this.beanClass = beanClass;
      this.values = values;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final String name = method.getName();
        if(method.getParameterCount()==0) {
            if(name.equals("hashCode")) return beanClass.hashCode() * 31 + values.hashCode();
            else if(name.equals("toString")) return beanClass.getSimpleName()+values;
            else return values.get(name);
        }
        else if(name.equals("equals") && method.getParameterCount()==1) {
            final Object other = args[0];
            if(!beanClass.isInstance(other)) return false;
            else if(Proxy.isProxyClass(other.getClass())) {
                final InvocationHandler oih = Proxy.getInvocationHandler(other);
                if(oih instanceof BeanInvocationHandler) {
                    return ((BeanInvocationHandler)oih).values.equals(values);
                }
                else return equals1(other);
            }
            else return equals1(other);
        }
        else throw new UnsupportedOperationException();
    }

    private Object equals1(final Object other) {
        return values.entrySet().stream()
            .allMatch(e->e.getValue().equals(call(()->beanClass.getMethod(e.getKey()).invoke(other))))
        ;
    }

}
