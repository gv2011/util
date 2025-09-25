package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.icol.ICollections.nothing;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.Nothing;

public class DefaultInvocationHandler<B> extends BeanInvocationHandlerSupport<B,Nothing> implements InvocationHandler{

  DefaultInvocationHandler(final BeanTypeSupport<B> beanType, final ISortedMap<String, Object> values) {
    super(beanType, values);
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    return handle(beanType.cast(proxy), method, args, nothing());
  }

  @Override
  protected Object handleOther(final Object proxy, final Method method, final Object[] args, final Nothing n){
    throw new UnsupportedOperationException(method.toString());
  }

}
