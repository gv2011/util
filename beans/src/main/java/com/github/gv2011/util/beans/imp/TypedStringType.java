package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.ex.Exceptions.bug;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Comparator;
import java.util.Optional;

import com.github.gv2011.util.beans.ElementaryTypeHandler;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.tstr.AbstractTypedString;
import com.github.gv2011.util.tstr.TypedString;

class TypedStringType<S extends TypedString<S>> extends AbstractElementaryType<S>{

  private final TypeHandler handler;
  private final Optional<S> defaultValue;

  TypedStringType(final JsonFactory jf, final Class<S> clazz) {
    super(jf, clazz);
    this.handler = new TypeHandler();
    this.defaultValue = Optional.of(create(""));
  }

  private class TypeHandler extends AbstractElementaryTypeHandler<S>{
    @Override
    public S fromJson(final JsonNode json) {
      return create(json.asString());
    }

    @Override
    public Optional<S> defaultValue() {
      return defaultValue;
    }
  }

  @Override
  ElementaryTypeHandler<S> handler(){return handler;}

  S create(final String value) {
    return clazz.cast(Proxy.newProxyInstance(
      clazz.getClassLoader(),
      new Class<?>[] {clazz},
      new TypedStringInvocationHandler(value))
    );
  }

  private class TypedStringInvocationHandler implements InvocationHandler{

    private final String value;

    TypedStringInvocationHandler(final String value) {
      this.value = value;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
      final Object result;
      final String name = method.getName();
      if(method.getParameterCount()==0) {
        if(name.equals("canonical")) result = value;
        else if(name.equals("toString")) result = value;
        else if(name.equals("hashCode")) result = AbstractTypedString.hashCode(clazz, value);
        else if(name.equals("clazz")) result = clazz;
        else if(name.equals("self")) result = proxy;
        else throw bug();
      }
      else if(method.getParameterCount()==1 && name.equals("compareTo")) {
        return ((Comparator)AbstractTypedString.COMPARATOR).compare(proxy, args[0]);
      }
      else throw bug();
      return result;
    }
  }


}
