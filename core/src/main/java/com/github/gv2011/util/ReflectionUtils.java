package com.github.gv2011.util;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
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




import static com.github.gv2011.util.CollectionUtils.iCollections;
import static com.github.gv2011.util.CollectionUtils.setBuilder;
import static com.github.gv2011.util.CollectionUtils.toISet;
import static com.github.gv2011.util.CollectionUtils.toISortedSet;
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.bugValue;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedSet;

public final class ReflectionUtils {

  private ReflectionUtils(){staticClass();}

  public static final Method EQUALS = call(()->Object.class.getMethod("equals", Object.class));
  public static final Method HASH_CODE = call(()->Object.class.getMethod("hashCode"));
  public static final Method TO_STRING = call(()->Object.class.getMethod("toString"));

  public static final ISet<Method> OBJECT_METHODS = iCollections().setOf(EQUALS,HASH_CODE,TO_STRING);

  public static <T> Method method(final Class<T> intf, final Function<T,?> methodFunction){
    return methodLookup(intf).method(notNull(methodFunction));
  }

  public static <T> MethodSignature signature(final Class<T> intf, final Function<T,?> methodFunction){
    return new MethodSignature(method(intf, methodFunction));
  }

  public static <T> String methodName(final Class<T> intf, final Function<T,?> methodFunction){
    return method(intf, methodFunction).getName();
  }

  public static final class Lookup<T>{
    private final T proxy;
    private final ThreadLocal<Method> method = new ThreadLocal<>();
    private Lookup(final Class<T> interfaze){
      final InvocationHandler ih = (proxy, method, args) -> {
        this.method.set(method);
        return defaultValue(method.getReturnType());
      };
      proxy = createProxy(interfaze, ih);
    }
    public Method method(final Function<T,?> methodFunction){
      methodFunction.apply(proxy);
      final Method result = notNull(method.get());
      return result;
    }
  }

  public static final @Nullable Object defaultValue(final Class<?> clazz) {
    if(clazz.isPrimitive()) {
      if(clazz==boolean.class) return false;
      else if(clazz==byte.class) return (byte)0;
      else if(clazz==short.class) return (short)0;
      else if(clazz==int.class) return (int)0;
      else if(clazz==long.class) return (long)0;
      else if(clazz==float.class) return (float)0;
      else if(clazz==double.class) return (double)0;
      else if(clazz==void.class) return null;
      else return bugValue();
    }
    else return null;
  }


  public static <T> Lookup<T> methodLookup(final Class<T> intf){
    return new Lookup<>(intf);
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
    final Method method = method(intf, methodFunction);
    final Object error = checkIsAttributeMethod(method);
    if(error!=null) throw new RuntimeException(error.toString());
    return method;
  }

  private static Object checkIsAttributeMethod(final Method method) {
    if(method.getParameterCount()!=0) return 1;
    return null;
  }

  public static Set<Class<?>> getAllInterfaces(final Class<?> clazz){
    final Set<Type> types = new HashSet<>();
    collectAllInterfaces(types, clazz);
    return types.stream()
      .flatMap(t->{
        if(t instanceof Class) return Stream.of((Class<?>)t);
        else if(t instanceof ParameterizedType) return Stream.of((Class<?>)((ParameterizedType)t).getRawType());
        else return Stream.<Class<?>>empty();
      })
      .collect(toISet())
    ;
  }

  public static Set<Type> getAllPInterfaces(final Class<?> clazz){
    final Set<Type> result = new HashSet<>();
    collectAllInterfaces(result, clazz);
    return Collections.unmodifiableSet(result);
  }

  private static void collectAllInterfaces(final Set<Type> result, final Type type){
    Optional<Class<?>> clazz;
    if(type instanceof ParameterizedType){
      clazz = Optional.of((Class<?>)((ParameterizedType)type).getRawType());
    }
    else if(type instanceof Class){
      clazz = Optional.of((Class<?>)type);
    }
    else clazz = Optional.empty();
    if(clazz.isPresent()){
      final Type superclass = clazz.get().getGenericSuperclass();
      if(superclass!=null){
        collectAllInterfaces(result, superclass);
      }
      final Set<Type> superInterfaces = Arrays.stream(clazz.get().getGenericInterfaces())
        .collect(toSet())
      ;
      for(final Type i: superInterfaces){
        final boolean added = result.add(i);
        if(added) collectAllInterfaces(result, i);
      }
    }
  }

  public static ISortedSet<String> toStrings(final ISet<Method> methods){
    final ISet.Builder<Class<?>> classes = setBuilder();
    for(final Method m: methods){
      classes.tryAdd(m.getDeclaringClass());
      classes.tryAdd(m.getReturnType());
      for(final Class<?> c: m.getParameterTypes()) classes.tryAdd(c);
    }
    final Function<Class<?>, String> nameShortener = nameShortener(classes.build());
    final ISortedSet<String> result = methods.stream()
      .map(m->
        m.getName() +
        XStream.of(m.getParameterTypes()).map(nameShortener).collect(joining(",","(",")")) +
        ":"+nameShortener.apply(m.getDeclaringClass())+"->"+nameShortener.apply(m.getReturnType())
      )
      .collect(toISortedSet())
    ;
    verify(result.size()==methods.size());
    return result;
  }

  public static Function<Class<?>,String> nameShortener(final ISet<Class<?>> classes){
    final ISet<Class<?>> notUnique;
    {
      final Set<String> simpleNames = new HashSet<>();
      final ISet.Builder<Class<?>> notUniqueB = setBuilder();
      for(final Class<?> c: classes){
        if(!simpleNames.add(c.getSimpleName())) notUniqueB.add(c);
      }
      notUnique = notUniqueB.build();
    }
    return c->notUnique.contains(c)?c.getName():c.getSimpleName();
  }

  public static Class<?> getWrapperClass(final Class<?> clazz) {
    if(clazz.isPrimitive()) {
      if(clazz==boolean.class) return Boolean.class;
      else if(clazz==byte.class) return Byte.class;
      else if(clazz==short.class) return Short.class;
      else if(clazz==int.class) return Integer.class;
      else if(clazz==long.class) return Long.class;
      else if(clazz==float.class) return Float.class;
      else if(clazz==double.class) return Double.class;
      else if(clazz==void.class) return Void.class;
      else return bugValue();
    }
    else return clazz;
  }

}
