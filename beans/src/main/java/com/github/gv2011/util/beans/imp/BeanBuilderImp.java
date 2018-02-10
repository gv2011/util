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
import static com.github.gv2011.util.CollectionUtils.iCollections;
import static com.github.gv2011.util.CollectionUtils.toISet;
import static com.github.gv2011.util.Verify.verify;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.gv2011.util.beans.BeanBuilder;
import com.github.gv2011.util.beans.Partial;
import com.github.gv2011.util.beans.Property;
import com.github.gv2011.util.icol.ISet;

final class BeanBuilderImp<T> implements BeanBuilder<T> {

    private final Map<String,Object> map = new HashMap<>();

    private final BeanTypeImp<T> beanInfo;


    BeanBuilderImp(final BeanTypeImp<T> beanInfo) {
        this.beanInfo = beanInfo;
    }

    @Override
    public T build() {
        for(final Property<?> p: beanInfo.properties().values()) {
            p.defaultValue().ifPresent(dv->{
                final String propName = p.name();
                if(!map.containsKey(propName)) map.put(p.name(), dv);
            });
        }
        final ISet<Property<?>> missing = beanInfo.properties().values().stream()
            .filter(p->!map.keySet().contains(p.name()))
            .collect(toISet())
        ;
        verify(missing, Set::isEmpty);
        return beanInfo.beanClass.cast(Proxy.newProxyInstance(
            beanInfo.beanClass.getClassLoader(),
            new Class<?>[] {beanInfo.beanClass},
            new BeanInvocationHandler(iCollections().copyOf(map))
        ));
    }

    @Override
    public Partial<T> buildPartial() {
        return new PartialImp<>(iCollections().copyOf(map));
    }

    @Override
    public <V> void set(final Property<V> p, final V value) {
        map.put(p.name(), value);
    }

}
