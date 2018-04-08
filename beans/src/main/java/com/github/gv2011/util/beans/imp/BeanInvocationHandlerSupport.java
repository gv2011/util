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
import java.util.Optional;

import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.icol.ISortedMap;

public abstract class BeanInvocationHandlerSupport<B,P>  {

  private final BeanTypeSupport<B> beanType;
  final ISortedMap<String, Object> values;

  private @Nullable Integer hashCode = null;

  protected BeanInvocationHandlerSupport(final BeanTypeSupport<B> beanType, final ISortedMap<String, Object> values) {
    this.beanType = beanType;
    this.values = values;
  }

  protected final Object handle(final Object proxy, final Method method, final Object[] args, final P x) throws Throwable {
    Object result;
    final String name = method.getName();
    if(method.getParameterCount()==0) {
      if(name.equals("hashCode")) result = getHashCode(proxy);
      else if(name.equals("toString")) result = beanType.clazz.getSimpleName()+values;
      else result = tryGetValue(name).orElseGet(()->handleOther(proxy, method, args, x));
    }
    else if(name.equals("equals") && method.getParameterCount()==1){
      result = handleEquals(proxy, notNull(args[0]));
    }
    else return handleOther(proxy, method, args, x);
    return result;
  }

  protected abstract Object handleOther(
     final Object proxy, final Method method, final Object[] args, P x
  );

  private int getHashCode(final Object proxy) {
    if(hashCode==null) hashCode = beanType.hashCode(beanType.cast(proxy));
    return hashCode.intValue();
  }

  private Optional<Object> tryGetValue(final String property) {
    if(beanType.properties().containsKey(property)){
      return Optional.of(
        values.tryGet(property).orElseGet(()->beanType.properties().get(property).defaultValue().get())
      );
    }
    else return Optional.empty();
  }

  private boolean handleEquals(final Object proxy, final Object other){
    boolean result;
    if(proxy==other) result = true;
    else if(!beanType.clazz.isInstance(other)) result = false;
    else{
      final B otherBean = beanType.clazz.cast(other);
      if(Proxy.isProxyClass(other.getClass())) {
        final InvocationHandler oih = Proxy.getInvocationHandler(other);
        if(oih instanceof BeanInvocationHandlerSupport) {
          final BeanInvocationHandlerSupport<?,?> obih = (BeanInvocationHandlerSupport<?,?>)oih;
          assert obih.beanType.equals(beanType);
          if(getHashCode(proxy)!=other.hashCode()){
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
      .allMatch(p->tryGetValue(p.name()).get().equals(p.getValue(other)))
    ;
  }

}
