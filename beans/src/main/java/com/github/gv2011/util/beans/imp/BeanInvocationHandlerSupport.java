package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.Verify.notNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.Opt;

public abstract class BeanInvocationHandlerSupport<B,P>  {

  private final BeanTypeSupport<B> beanType;
  final ISortedMap<String, Object> values;
  private final Constant<Bean> key;

  private @Nullable Integer hashCode = null;

  protected BeanInvocationHandlerSupport(final BeanTypeSupport<B> beanType, final ISortedMap<String, Object> values) {
    this.beanType = beanType;
    this.values = values;
    key = Constants.cachedConstant(()->beanType.getKey(values));
  }

  protected final Object handle(final Object proxy, final Method method, final Object[] args, final P x) throws Throwable {
    Object result;
    final String name = method.getName();
    if(method.getParameterCount()==0) {
      if(name.equals("hashCode")) result = getHashCode(proxy);
      else if(name.equals("toString")) result = handleToString(proxy, method, args, x);
      else if(beanType.isKeyBean() && name.equals(BeanFactory.KEY_METHOD_NAME)) result = key.get();
      else result = tryGetValue(proxy, name).orElseGet(()->handleOther(proxy, method, args, x));
    }
    else if(name.equals("equals") && method.getParameterCount()==1){
      result = handleEquals(proxy, notNull(args[0]));
    }
    else return handleOther(proxy, method, args, x);
    return result;
  }

  protected String handleToString(final Object proxy, final Method method, final Object[] args, final P x) {
    return beanType.clazz.getSimpleName()+values;
  }

  protected abstract Object handleOther(
     final Object proxy, final Method method, final Object[] args, P x
  );

  private int getHashCode(final Object proxy) {
    if(hashCode==null) hashCode = beanType.hashCode(beanType.cast(proxy));
    return hashCode.intValue();
  }

  private Optional<Object> tryGetValue(final Object proxy, final String property) {
    if(beanType.properties().containsKey(property)){
      return Optional.of(
        values.tryGet(property).orElseGet(()->{
          final PropertyImp<B, ?> prop = beanType.properties().get(property);
          final Opt<?> defaultValue = prop.defaultValue();
          if(defaultValue.isPresent()) return defaultValue.get();
          else return prop.function().get().apply(beanType.cast(proxy));
        })
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
//            assert result == equals1(proxy, otherBean);
            if(!(result == equals1(proxy, otherBean))){
              assert false;
            }
          }
          else{
            result = obih.values.equals(values);
            assert result == equals1(proxy, otherBean);
          }
        }
        else result = equals1(proxy, otherBean);
      }
      else result = equals1(proxy, otherBean);
    }
    return result;
  }

  private boolean equals1(final Object proxy, final B other) {
    return beanType.properties().values().stream()
      .filter(p->!p.computed())
      .allMatch(p->tryGetValue(proxy, p.name()).get().equals(p.getValue(other)))
    ;
  }

}
