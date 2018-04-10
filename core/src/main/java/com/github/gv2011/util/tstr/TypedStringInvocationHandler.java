package com.github.gv2011.util.tstr;

import static com.github.gv2011.util.Verify.notNull;
/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
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

final class TypedStringInvocationHandler<S extends TypedString<S>> implements InvocationHandler{

    static final <S extends TypedString<S>> S create(final Class<S> clazz, final String value) {
        return clazz.cast(Proxy.newProxyInstance(
            clazz.getClassLoader(),
            new Class<?>[] {clazz},
            new TypedStringInvocationHandler<>(clazz, value)
        ));
    }

    private final Class<S> clazz;
    private final String value;

    private TypedStringInvocationHandler(final Class<S> clazz, final String value) {
        this.clazz = clazz;
        this.value = value;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
      final Object result;
      final String name = notNull(method).getName();
      if(method.getParameterCount()==0) {
        if(name.equals("canonical")) result = value;
        else if(name.equals("isEmpty")) result = value.isEmpty();
        else if(name.equals("toString")) result = value;
        else if(name.equals("hashCode")) result = TypedString.hashCode(clazz, value);
        else if(name.equals("clazz")) result = clazz;
        else if(name.equals("self")) result = proxy;
        else if(name.equals("chars")) result = value.chars();
        else if(name.equals("codePoints")) result = value.codePoints();
        else if(name.equals("length")) result = value.length();
        else if(name.equals("self")) result = proxy;
        else throw bug(()->name);
      }
      else if(method.getParameterCount()==1) {
        if(name.equals("compareTo")) {
          result = ((Comparator)TypedString.COMPARATOR).compare(proxy, args[0]);
        }
        else if(name.equals("compareWithOtherOfSameType")) {
          result = ((TypedString<?>)proxy).canonical().compareTo(((TypedString<?>)args[0]).canonical());
        }
        else if(name.equals("equals")) {
          result =  TypedString.equal((TypedString<?>) proxy, args[0]);
        }
        else if(name.equals("charAt")) {
          result =  value.charAt((Integer)args[0]);
        }
        else throw bug(method::toString);
      }
      else if(method.getParameterCount()==2 && name.equals("subSequence")) {
        result =  value.subSequence((Integer)args[0], (Integer)args[1]);
      }
      else throw bug();
      return result;
    }
  }

