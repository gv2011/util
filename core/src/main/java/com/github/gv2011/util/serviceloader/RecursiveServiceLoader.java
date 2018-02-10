package com.github.gv2011.util.serviceloader;

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

import static com.github.gv2011.util.CollectionUtils.iCollections;
import static com.github.gv2011.util.CollectionUtils.mapBuilder;
import static com.github.gv2011.util.CollectionUtils.toISet;
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;

import java.lang.reflect.Constructor;
import java.util.Map.Entry;
import java.util.Optional;

import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.IMap.Builder;
import com.github.gv2011.util.icol.ISet;

public final class RecursiveServiceLoader {

    private static final RecursiveServiceLoader INSTANCE = new RecursiveServiceLoader();

    public static final <S> S service(final Class<S> serviceClass) {
        return INSTANCE.getService(serviceClass);
    }

    private IMap<Class<?>,ISet<?>> services = iCollections().emptyMap();

    private RecursiveServiceLoader() {}

    private <S> S getService(final Class<S> serviceClass) {
        return getServices(serviceClass).single();
    }

    @SuppressWarnings("unchecked")
    private <S> ISet<S> getServices(final Class<S> serviceClass) {
        Optional<ISet<?>> entry = services.tryGet(serviceClass);
        if(!entry.isPresent()) {
            synchronized(this) {
                entry = services.tryGet(serviceClass);
                if(!entry.isPresent()) {
                    loadServices(serviceClass);
                    entry = Optional.of(services.get(serviceClass));
                }
            }
        }
        entry.get();
        return (ISet<S>) entry.get();
    }

    private <S> void loadServices(final Class<S> serviceClass) {
        final ISet<S> implementations = ServiceProviderConfigurationFile.files(serviceClass).stream()
            .flatMap(f->f.implementations().stream())
            .map(n->createInstance(serviceClass, n))
            .collect(toISet())
        ;
        final Builder<Class<?>,ISet<?>> b = mapBuilder();
        for(final Entry<Class<?>, ISet<?>> e: services.entrySet()) b.put(e.getKey(), e.getValue());
        //b.putAll(services);
        b.put(serviceClass, implementations);
        services = b.build();
    }

    private <S> S createInstance(final Class<S> serviceClass, final String implementationClassName) {
        final Class<?> implClass = call(()->Class.forName(implementationClassName));
        verify(serviceClass.isAssignableFrom(implClass));
        final Constructor<?> constr = single(implClass.getConstructors());
        final Class<?>[] pTypes = constr.getParameterTypes();
        final Object[] initargs = new Object[pTypes.length];
        for(int i=0; i<initargs.length; i++) {
            initargs[i] = getService(pTypes[i]);
        }
        return serviceClass.cast(call(()->constr.newInstance(initargs)));
    }

    private static final <T> T single(final T[] array) {
        verify(array.length==1);
        return notNull(array[0]);
    }

}
