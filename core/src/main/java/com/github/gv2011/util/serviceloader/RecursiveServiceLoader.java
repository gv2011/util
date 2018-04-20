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

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static java.util.stream.Collectors.toSet;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.github.gv2011.util.CachedConstant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.icol.IEmpty;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.icol.Single;


public final class RecursiveServiceLoader {

    private static final CachedConstant<RecursiveServiceLoader> INSTANCE =
      Constants.cachedConstant(RecursiveServiceLoader::new)
    ;

    public static final <S> S service(final Class<S> serviceClass) {
        return INSTANCE.get().getService(serviceClass);
    }

    public static final <S> Opt<S> tryGetService(final Class<S> serviceClass) {
        return INSTANCE.get().tryGetServiceInternal(serviceClass);
    }

    private final Object lock = new Object();

    private final Map<Class<?>,Set<?>> services = new HashMap<>();

    private final Set<Class<?>> loading = new HashSet<>();

    private RecursiveServiceLoader() {}

    private <S> S getService(final Class<S> serviceClass) {
      return tryGetServiceInternal(serviceClass)
        .orElseThrow(()->new IllegalStateException(format("No implementation for {} found.", serviceClass))
      );
    }

    @SuppressWarnings("unchecked")
    private <S> Opt<S> tryGetServiceInternal(final Class<S> serviceClass) {
        final Set<S> services = getServices(serviceClass);
        if(services.isEmpty()) return IEmpty.INSTANCE;
        else{
          verify(services, s->s.size()==1, s->format("Multiple implementations for service {}: {}.", serviceClass, s));
          return Single.of(services.iterator().next());
        }
    }

    @SuppressWarnings("unchecked")
    private <S> Set<S> getServices(final Class<S> serviceClass) {
        Opt<Set<?>> entry = Single.ofNullable(services.get(serviceClass));
        if(!entry.isPresent()) {
            synchronized(lock) {
                entry = Single.ofNullable(services.get(serviceClass));
                if(!entry.isPresent()) {
                  final boolean added = loading.add(serviceClass);
                  try{
                    if(!added) {throw new RuntimeException(
                        format("Infinite recursion: already loading {}.", serviceClass.getName()));
                      }
                    loadServices(serviceClass);
                    entry = Single.of(services.get(serviceClass));
                  }
                  finally{loading.remove(serviceClass);}
                }
            }
        }
        entry.get();
        return (Set<S>) entry.get();
    }

    private <S> void loadServices(final Class<S> serviceClass) {
        final Set<S> implementations = Collections.unmodifiableSet(
            ServiceProviderConfigurationFile.filesInternal(serviceClass)
            .flatMap(f->f.implementationsInternal())
            .map(n->createInstance(serviceClass, n))
            .collect(toSet())
        );
        services.put(serviceClass, implementations);
    }

    private <S> S createInstance(final Class<S> serviceClass, final String implementationClassName) {
        final Class<?> implClass = call(()->Class.forName(implementationClassName));
        verify(serviceClass.isAssignableFrom(implClass));
        final Constructor<?> constr = Arrays.stream(implClass.getConstructors())
            .filter(c->c.getParameterCount()==0)
            .findAny()
            .orElseThrow(()->new NoSuchElementException(format("{} has no no-arg constructor.", implClass)))
        ;
        final Class<?>[] pTypes = constr.getParameterTypes();
        final Object[] initargs = new Object[pTypes.length];
        for(int i=0; i<initargs.length; i++) {
            initargs[i] = getService(pTypes[i]);
        }
        return serviceClass.cast(call(()->constr.newInstance(initargs)));
    }

}
