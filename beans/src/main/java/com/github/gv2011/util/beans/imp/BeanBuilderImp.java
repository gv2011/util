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
import static com.github.gv2011.util.Verify.verifyEqual;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.github.gv2011.util.beans.BeanBuilder;
import com.github.gv2011.util.beans.Partial;
import com.github.gv2011.util.beans.Property;
import com.github.gv2011.util.icol.ISet;

final class BeanBuilderImp<T> implements BeanBuilder<T> {


    private final Map<String,Object> map = new HashMap<>();

    private final BeanTypeImp<T> beanType;


    BeanBuilderImp(final BeanTypeImp<T> beanInfo) {
        this.beanType = beanInfo;
    }

    @Override
    public T build() {
        for(final Property<?> p: beanType.properties().values()) {
            p.defaultValue().ifPresent(dv->{
                final String propName = p.name();
                if(!map.containsKey(propName)) map.put(p.name(), dv);
            });
        }
        final ISet<Property<?>> missing = beanType.properties().values().stream()
            .filter(p->!map.keySet().contains(p.name()))
            .collect(toISet())
        ;
        verify(missing, Set::isEmpty);
        return beanType.clazz.cast(Proxy.newProxyInstance(
            beanType.clazz.getClassLoader(),
            new Class<?>[] {beanType.clazz},
            new BeanInvocationHandler(beanType.clazz, iCollections().copyOf(map))
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

    @Override
    public <V> Setter<V> set(final Function<T, V> method) {
      final PropertyImp<V> property = beanType.getProperty(method);
      return new SetterImp<>(property);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <V> Setter<V> setOpt(final Function<T, Optional<V>> method) {
      final PropertyImp<Optional<V>> property = beanType.getProperty(method);
      verify(property.type() instanceof CollectionType);
      final CollectionType optType = (CollectionType) property.type();
      verifyEqual(optType.structure, Structure.opt());
      return new OptSetter<>(property);
    }

    private class SetterImp<V> implements Setter<V> {
      private final PropertyImp<V> property;
      private SetterImp(final PropertyImp<V> property) {
        this.property = property;
      }
      @Override
      public void to(final V value) {
        map.put(property.name(), property.type().cast(value));
      }
    }

    private class OptSetter<V> implements Setter<V> {
      private final PropertyImp<Optional<V>> property;
      private OptSetter(final PropertyImp<Optional<V>> property) {
        this.property = property;
      }
      @SuppressWarnings("rawtypes")
      @Override
      public void to(final V value) {
        map.put(
          property.name(),
          Optional.of(((CollectionType) property.type()).elementType().cast(value))
        );
      }
    }

}
