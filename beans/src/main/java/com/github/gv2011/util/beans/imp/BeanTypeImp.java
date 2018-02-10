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
import static com.github.gv2011.util.CollectionUtils.setOf;
import static com.github.gv2011.util.CollectionUtils.toISortedMap;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.github.gv2011.util.beans.BeanBuilder;
import com.github.gv2011.util.beans.BeanType;
import com.github.gv2011.util.beans.DefaultValue;
import com.github.gv2011.util.beans.Partial;
import com.github.gv2011.util.beans.Property;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;
import com.github.gv2011.util.json.JsonObject;

class BeanTypeImp<T> extends AbstractType<T> implements BeanType<T> {

    private static final ISet<String> RESERVED = setOf();

    @SuppressWarnings("rawtypes")
    private static final Partial EMPTY_PARTIAL = createEmptyPartial();

    private static final ISet<Character> JSON_START = setOf('\"','[','{');

    private final ISortedMap<String, PropertyImp<?>> properties;

    private final Optional<T> defaultValue;


    BeanTypeImp(final Class<T> beanClass, final DefaultTypeRegistry bif) {
        super(bif.jf, beanClass);
        verify(beanClass.isInterface(), beanClass::toString);
        this.properties = getProperties(beanClass, bif);
        defaultValue = createDefaultValue();
    }

    private Optional<T> createDefaultValue() {
      if(properties.values().stream().allMatch(p->p.defaultValue().isPresent())) {
        final BeanBuilder<T> b = createBuilder();
        for(final PropertyImp<?> p: properties.values()) setDefaultValue(b, p);
        return Optional.of(b.build());
      }
      else return Optional.empty();
    }

    private <V> void setDefaultValue(final BeanBuilder<T> b, final PropertyImp<V> p) {
      setValue(b, p, p.defaultValue().get());
    }


    @Override
    public BeanBuilder<T> createBuilder() {
        return new BeanBuilderImp<>(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Partial<T> emptyPartial() {
        return EMPTY_PARTIAL;
    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public ISortedMap<String, Property<?>> properties() {
        return (ISortedMap) properties;
    }

    private static <T> ISortedMap<String, PropertyImp<?>> getProperties(
      final Class<T> beanClass, final DefaultTypeRegistry bif
    ) {
      return Arrays.stream(beanClass.getMethods())
      .filter(BeanTypeImp::isPropertyMethod)
      .map(m->createProperty(beanClass, m, bif))
      .collect(toISortedMap(
          p->p.name(),
          p->p
      ));
    }

    private static boolean isPropertyMethod(final Method m) {
        return m.getParameterCount()>0 ? false : ! RESERVED.contains(m.getName());
    }

    private static <V> PropertyImp<V> createProperty(
      final Class<?> beanClass, final Method m, final DefaultTypeRegistry typeRegistry
    ) {
      @SuppressWarnings("unchecked")
      final AbstractType<V> type = (AbstractType<V>) typeRegistry.type(m.getGenericReturnType());
      final Optional<V> defaultValue =
          Optional.ofNullable(m.getAnnotation(DefaultValue.class))
          .map(a->type.parse(parseTolerant(type, typeRegistry.jf, a.value())))
      ;
      return new PropertyImp<>(m.getName(), type, defaultValue);
    }

    private static JsonNode parseTolerant(final AbstractType<?> type, final JsonFactory jf, final String string) {
      JsonNode result;
      final String trimmed = string.trim();
      if(trimmed.isEmpty()) {
        result = jf.primitive(string);
      }
      else {
        final char start = trimmed.charAt(0);
        if(!JSON_START.contains(start) && type instanceof ElementaryTypeImp) {
          final ElementaryTypeImp<?> et = (ElementaryTypeImp<?>)type;
          if(!et.jsonNodeType().equals(JsonNodeType.STRING)) {
            result = jf.deserialize(string);
          }
          else result = jf.primitive(string);
        }
        else result = jf.deserialize(string);
      }
      return result;
    }

    @Override
    public T parse(final JsonNode json) {
      final JsonObject obj = (JsonObject) json;
      final BeanBuilder<T> b = createBuilder();
      for(final Property<?> p: properties.values()) {
        final Optional<JsonNode> childNode = obj.tryGet(p.name());
        if(childNode.isPresent()) {
          setJsonValue(b, p, childNode.get());
        }
      }
      return b.build();
    }

    private <V> void setJsonValue(final BeanBuilder<T> b, final Property<V> p, final JsonNode json) {
      setValue(b, p, p.type().parse(json));
    }

    private <V> void setValue(final BeanBuilder<T> b, final Property<V> p, final V value) {
      b.set(p, value);
    }


    @Override
    public JsonObject toJson(final T object) {
      return properties.values().stream()
        .collect(jf.toJsonObject(
          p->(String)p.name(),
          p->toJson(object, p)
        ))
      ;
    }

    @Override
    public boolean isDefault(final T obj) {
      return properties.values().stream().allMatch(p->isDefault(obj, p));
    }



    @Override
    public Optional<T> getDefault() {
      return defaultValue;
    }


    private <V> boolean isDefault(final T bean, final PropertyImp<V> property) {
      return property.type().isDefault(get(bean, property));
    }

    private <V> JsonNode toJson(final T bean, final Property<V> property) {
      return property.type().toJson(get(bean, property));
    }

    @Override
    public <V> V get(final T bean, final Property<V> property) {
      return property.type().cast(call(()->beanClass.getMethod(property.name()).invoke(bean)));
    }


    private static <B> Partial<B> createEmptyPartial() {
        return new Partial<B>() {
            @Override
            public boolean isAvailable(final Property<?> p) {return false;}
            @Override
            public boolean isAvailable(final String propertyName) {return false;}
            @Override
            public <T> T get(final Property<T> p) {throw new NoSuchElementException();}
        };
    }

}
