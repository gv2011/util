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
import static com.github.gv2011.util.CollectionUtils.stream;
import static com.github.gv2011.util.CollectionUtils.toOpt;
import static com.github.gv2011.util.ex.Exceptions.call;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Function;

import com.github.gv2011.util.icol.Opt;

final class AutoElementarySupport {

    boolean isSupported(final Class<?> clazz) {
       return tryGetStringConstructor(clazz).isPresent();
    }

    private <T> Opt<Function<String,T>> tryGetStringConstructor(final Class<T> clazz) {
        final Opt<Function<String,T>> result;
        final Opt<Constructor<?>> constructor = stream(clazz.getConstructors())
          .filter(c->c.getParameterTypes().length==1)
          .filter(c->c.getParameterTypes()[0].equals(String.class))
          .collect(toOpt())
        ;
        if(constructor.isPresent()) {
            result = Opt.of(s->call(()->clazz.cast(constructor.get().newInstance())));
        }
        else{
          final Opt<Method> factoryMethod = stream(clazz.getMethods())
            .filter(m->Modifier.isStatic(m.getModifiers()))
            .filter(m->m.getName().equals("parse"))
            .filter(m->m.getParameterTypes().length==1)
            .filter(m->m.getParameterTypes()[0].equals(String.class))
            .filter(m->clazz.isAssignableFrom(m.getReturnType()))
            .collect(toOpt())
          ;
          result = factoryMethod.map(m->s->clazz.cast(call(()->m.invoke(null, s))));
        }
        return result;
      }

    private <T> Opt<T> defaultValue(final Function<String,T> constructor){
        try{
            return Opt.of(constructor.apply(""));
        }catch(final Exception ex) {
            return Opt.empty();
        }
    }

    <T> AbstractElementaryTypeHandler<T> createType(final Class<T> clazz) {
        final Function<String, T> constructor = tryGetStringConstructor(clazz).get();
        return DefaultElementaryTypeHandlerFactory.stringBasedType(constructor, defaultValue(constructor));
    }
}
