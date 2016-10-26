package com.github.gv2011.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.sql.Struct;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ReflectionUtils {

public static <T> Method method(final Class<T> intf, final Function<T,?> methodFunction){
  final AtomicReference<Method> result = new AtomicReference<>();
  final InvocationHandler ih = (proxy, method, args) -> {
    result.set(method);
    return null;
    };
  final T proxy = createProxy(intf, ih);
  methodFunction.apply(proxy);
  return result.get();
  }

public static <T> T createProxy(final Class<T> intf, final InvocationHandler ih) {
  return intf.cast(Proxy.newProxyInstance(intf.getClassLoader(), new Class<?>[]{intf}, ih));
  }

public static Method getOwnMethod(final Object target, final Method method, final Object [] parameters){
  try {
    final Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
    return targetMethod;
    }
  catch (IllegalArgumentException | NoSuchMethodException | SecurityException e) {
    throw new RuntimeException(e);
    }
  }

public static <T> Method attributeMethod(final Class<T> intf, final Function<T,?> methodFunction){
  checkIsStruct(intf);
  final Method method = method(intf, methodFunction);
  final Object error = checkIsAttributeMethod(method);
  if(error!=null) throw new RuntimeException(error.toString());
  return method;
  }

private static Object checkIsAttributeMethod(final Method method) {
  if(method.getParameterCount()!=0) return 1;
  return null;
  }

public static void checkIsStruct(final Class<?> intf){
  if(!intf.isInterface()) throw new IllegalArgumentException();
  final Class<?>[] interfaces = intf.getInterfaces();
  if(interfaces.length!=1) throw new IllegalArgumentException();
  if(interfaces[0]!=Struct.class) throw new IllegalArgumentException();
  final java.lang.reflect.Type pStruct1 = intf.getGenericInterfaces()[0];
  if(!(pStruct1 instanceof ParameterizedType)) throw new IllegalArgumentException();
  final ParameterizedType pStruct = (ParameterizedType) pStruct1;
  final java.lang.reflect.Type[] args = pStruct.getActualTypeArguments();
  if(args.length!=1) throw new IllegalArgumentException();
  if(!args[0].equals(intf)) throw new IllegalArgumentException();
  }

public static Set<Class<?>> getAllSuperInterfaces(final Class<?> clazz){
  return Arrays.stream(clazz.getInterfaces())
    .flatMap(i->getAllSuperInterfaces(i).stream())
    .collect(Collectors.toSet())
  ;
  }
}
