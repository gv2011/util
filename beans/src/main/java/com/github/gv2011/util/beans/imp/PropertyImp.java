package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.bug;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
import java.util.Optional;
import java.util.function.Function;

import com.github.gv2011.util.beans.Property;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.icol.ISortedMap;

public final class PropertyImp<B,T> implements Property<T> {

    private final Method method;
    private final String name;
    private final TypeSupport<T> type;
    private final Opt<T> defaultValue;
    private final Opt<T> fixedValue;
    private final Opt<Function<B,T>> function;

    static <B,T> PropertyImp<B,T> create(
      final BeanTypeSupport<B> owner,
      final Method method,
      final TypeSupport<T> type,
      final Opt<T> defaultValue
    ) {
      return new PropertyImp<>(owner, method, method.getName(), type, defaultValue, Opt.empty(), Opt.empty());
    }

    static <B,T> PropertyImp<B,T> createComputed(
        final BeanTypeSupport<B> owner,
        final Method method,
        final TypeSupport<T> type,
        final Function<B,T> function
    ) {
      return new PropertyImp<>(owner, method, method.getName(), type, Opt.empty(), Opt.empty(), Opt.of(function));
    }

    static <B,T> PropertyImp<B,T> createFixed(
      final BeanTypeSupport<B> owner,
      final Method method,
      final String name,
      final TypeSupport<T> type,
      final T fixedValue
    ) {
      final Opt<T> fixed = Opt.of(fixedValue);
      return new PropertyImp<>(owner, method, name, type, fixed, fixed, Opt.empty());
    }

    private PropertyImp(
      final BeanTypeSupport<B> owner,
      final Method method,
      final String name,
      final TypeSupport<T> type,
      final Opt<T> defaultValue,
      final Opt<T> fixedValue,
      final Opt<Function<B,T>> function
    ) {
      this.method = method;
      this.name = name;
      this.type = type;
      assert fixedValue.isPresent() ? defaultValue.get().equals(fixedValue.get()) : true;
      this.defaultValue = defaultValue;
      this.fixedValue = fixedValue;
      this.function = function;
      verify(type.isForeignType() ? function.isPresent() : true);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public TypeSupport<T> type() {
        return type;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Opt<T> defaultValue() {
      return defaultValue;
    }

    @Override
    public Opt<T> fixedValue() {
       return fixedValue;
    }

    public Opt<Function<B,T>> function() {
      return function;
    }

    boolean isOptional() {
      return type().isOptional();
    }

    T getValue(final B bean){
      return fixedValue()
        .map(v->{
          assert getNonFixedValue(bean).equals(v);
          return v;
        })
        .orElseGet(()->{
          return getNonFixedValue(bean);
        })
      ;
    }

    private T getNonFixedValue(final B bean) {
      return tryGetMap(bean)
        .map(map->{
          final T fromMapOrDefault = map
            .tryGet(name()).map(type()::cast)
            .orElseGet(()->defaultValue().orElseThrow(()->bug(()->format("Empty property: {}.", this))))
          ;
          assert fromMapOrDefault.equals(invokeMethod(bean));
          return fromMapOrDefault;
        })
        .orElseGet(()->invokeMethod(bean))
      ;
    }

    private T invokeMethod(final B bean) {
      return type.cast(call(()->method.invoke(bean)));
    }

    private Optional<ISortedMap<String, Object>> tryGetMap(final B bean) {
      Optional<ISortedMap<String, Object>> result;
      if(Proxy.isProxyClass(bean.getClass())){
        final InvocationHandler ih = Proxy.getInvocationHandler(bean);
        if(ih.getClass().equals(BeanInvocationHandlerSupport.class)){
          result = Optional.of(((BeanInvocationHandlerSupport<?,?>)ih).values);
        }
        else result = Optional.empty();
      }
      else result = Optional.empty();
      return result;
    }

}
