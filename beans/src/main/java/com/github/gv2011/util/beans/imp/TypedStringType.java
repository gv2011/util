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
