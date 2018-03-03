package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.ex.Exceptions.call;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.icol.ISortedMap;

final class BeanInvocationHandler<B> implements InvocationHandler {

    private final DefaultBeanType<B> beanType;
    private final ISortedMap<String, Object> values;

    private @Nullable Integer hashCode = null;

    BeanInvocationHandler(final DefaultBeanType<B> beanType, final ISortedMap<String, Object> values) {
      this.beanType = beanType;
      this.values = values;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final String name = method.getName();
        if(method.getParameterCount()==0) {
            if(name.equals("hashCode")) return getHashCode();
            else if(name.equals("toString")) return beanType.clazz.getSimpleName()+values;
            else return getValue(name);
        }
        else if(name.equals("equals") && method.getParameterCount()==1) {
            final Object other = args[0];
            boolean result;
            if(proxy==other) result = true;
            else if(getHashCode()!=other.hashCode()) result = false;
            else {
                if(!beanType.clazz.isInstance(other)) result = false;
                else if(Proxy.isProxyClass(other.getClass())) {
                    final InvocationHandler oih = Proxy.getInvocationHandler(other);
                    if(oih.getClass().equals(BeanInvocationHandler.class)) {
                        final BeanInvocationHandler<?> obih = (BeanInvocationHandler<?>)oih;
                        result = obih.beanType.equals(beanType) && obih.values.equals(values);
                    }
                    else result = equals1(other);
                }
                else result = equals1(other);
            }
            assert result == equals1(other);
            return result;
        }
        else throw new UnsupportedOperationException(method.toString());
    }

    private int getHashCode() {
      if(hashCode==null) hashCode = beanType.clazz.hashCode() * 31 + values.hashCode();
      return hashCode.intValue();
    }

    private Object getValue(final String property) {
      assert beanType.properties().containsKey(property);
      return values.tryGet(property).orElseGet(()->beanType.properties().get(property).defaultValue().get());
    }

    private boolean equals1(final Object other) {
      return beanType.properties().keySet().stream()
        .allMatch(p->getValue(p).equals(call(()->beanType.clazz.getMethod(p).invoke(other))))
      ;
    }

}
