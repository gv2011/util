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
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.format;

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

    final DefaultBeanType<T> beanType;

    BeanBuilderImp(final DefaultBeanType<T> beanInfo) {
        this.beanType = beanInfo;
    }

    @Override
    public T build() {
      //fill in fixed values:
      for(final Property<?> p: beanType.properties().values()) {
          p.fixedValue().ifPresent(fv->{
              final String propName = p.name();
              if(map.containsKey(propName)) verify(map.get(propName).equals(fv));
              else map.put(p.name(), fv);
          });
      }
      //fill in default values:
      for(final Property<?> p: beanType.properties().values()) {
          p.defaultValue().ifPresent(dv->{
              final String propName = p.name();
              if(!map.containsKey(propName)) map.put(p.name(), dv);
          });
      }
      //verify all properties are set:
      final ISet<Property<?>> missing = beanType.properties().values().stream()
          .filter(p->!map.keySet().contains(p.name()))
          .collect(toISet())
      ;
      verify(missing, Set::isEmpty);
      //create proxy:
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
    public BeanBuilder<T> setAll(final T bean) {
        for(final PropertyImp<?> p: beanType.properties().values()) {
            copy(p, bean);
        }
        return this;
    }

    private <V >void copy(final PropertyImp<V> p, final T bean) {
        map.put(p.name(), beanType.get(bean, p));
    }

    @Override
    public <V> void set(final Property<V> p, final V value) {
        map.put(p.name(), value);
    }

    @Override
    public <V> Setter<T,V> set(final Function<T, V> method) {
      final PropertyImp<V> property = beanType.getProperty(method);
      return new SetterImp<>(property);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <V> Setter<T,V> setOpt(final Function<T, Optional<V>> method) {
      final PropertyImp<Optional<V>> property = beanType.getProperty(method);
      verify(property.type() instanceof CollectionType);
      final CollectionType optType = (CollectionType) property.type();
      verifyEqual(optType.structure, Structure.opt());
      return new OptSetter<>(property);
    }

    private class SetterImp<V> implements Setter<T,V> {
      private final PropertyImp<V> property;
      private SetterImp(final PropertyImp<V> property) {
        this.property = property;
      }
      @Override
      public BeanBuilder<T> to(final V value) {
        map.put(
            property.name(),
            property.type().cast(notNull(value, ()->format("Trying to set {} to null.", property)))
        );
        return BeanBuilderImp.this;
      }
    }

    private class OptSetter<V> implements Setter<T,V> {
      private final PropertyImp<Optional<V>> property;
      private OptSetter(final PropertyImp<Optional<V>> property) {
        this.property = property;
      }
      @SuppressWarnings("rawtypes")
      @Override
      public BeanBuilder<T> to(final V value) {
        map.put(
          property.name(),
          Optional.of(((CollectionType) property.type()).elementType().cast(notNull(value)))
        );
        return BeanBuilderImp.this;
      }
    }

}