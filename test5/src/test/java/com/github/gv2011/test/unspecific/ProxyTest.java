package com.github.gv2011.test.unspecific;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.wrap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;

import com.github.gv2011.util.ReflectionUtils;

class ProxyTest {

  static interface Example{
    default String hello(){return "Hello!";}
  }

  @Test
  void test() {
    ReflectionUtils.method(Example.class, Example::hello);
    final Example ex = (Example) Proxy.newProxyInstance(
      Example.class.getClassLoader(),
      new Class[]{Example.class},
      (InvocationHandler)(final Object proxy, final Method method, final Object[] args)->{
        assertThat(method.getName(), is("hello"));
        assertTrue(method.isDefault());

        return getDefaultMethodInvoker(method).apply(proxy, args);
      }
    );
    assertThat(ex.hello(), is("Hello!"));
  }

    private static <B> BiFunction<B, Object[],Object> getDefaultMethodInvoker(final Method method){
      assert method.isDefault();
      final MethodHandle special = call(()->MethodHandles.lookup().findSpecial(
        method.getDeclaringClass(),
        method.getName(),
        MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
        method.getDeclaringClass()
      ));
      return (proxy, args) -> {
        try {
          return special.bindTo(proxy).invokeWithArguments(args);
        } catch (final Throwable e) {
          throw wrap(e);
        }
      };
    }

}
