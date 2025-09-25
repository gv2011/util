package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.ex.Exceptions.bug;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.function.Function;

import com.github.gv2011.util.beans.Property;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.Opt;

public final class PropertyImp<B,T> implements Property<T> {

    private final Method method;
    private final String name;
    private final TypeSupport<T> type;
    private final Opt<T> defaultValue;
    private final Opt<T> fixedValue;
    private final Opt<Function<B,T>> function;
    private final boolean isKey;

    static <B,T> PropertyImp<B,T> create(
      final BeanTypeSupport<B> owner,
      final Method method,
      final TypeSupport<T> type,
      final Opt<T> defaultValue,
      final boolean isKey
    ) {
      return new PropertyImp<>(owner, method, method.getName(), type, defaultValue, Opt.empty(), Opt.empty(), isKey);
    }

    static <B,T> PropertyImp<B,T> createComputed(
        final BeanTypeSupport<B> owner,
        final Method method,
        final TypeSupport<T> type,
        final Function<B,T> function
    ) {
      return new PropertyImp<>(owner, method, method.getName(), type, Opt.empty(), Opt.empty(), Opt.of(function), false);
    }

    static <B,T> PropertyImp<B,T> createFixed(
      final BeanTypeSupport<B> owner,
      final Method method,
      final String name,
      final TypeSupport<T> type,
      final T fixedValue
    ) {
      final Opt<T> fixed = Opt.of(fixedValue);
      return new PropertyImp<>(owner, method, name, type, fixed, fixed, Opt.empty(), false);
    }

    private PropertyImp(
      final BeanTypeSupport<B> owner,
      final Method method,
      final String name,
      final TypeSupport<T> type,
      final Opt<T> defaultValue,
      final Opt<T> fixedValue,
      final Opt<Function<B,T>> function,
      final boolean isKey
    ) {
      this.method = method;
      this.name = name;
      this.type = type;
      assert fixedValue.isPresent() ? defaultValue.get().equals(fixedValue.get()) : true;
      this.defaultValue = defaultValue;
      this.fixedValue = fixedValue;
      this.function = function;
      this.isKey = isKey;
//      verify(
//        type.isForeignType() ? function.isPresent() : true,
//        ()->format(
//          "Property {} of type {}: The value type {} of this property "+
//          "is a foreign type, but no computing function is given.",
//          name, owner, type
//        )
//      );
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

    public boolean computed() {
      return function.isPresent();
    }

    boolean isOptional() {
      return type().isOptional();
    }

    @Override
    public boolean isKey() {
      return isKey;
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
