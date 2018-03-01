package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.CollectionUtils.pair;
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
import static com.github.gv2011.util.CollectionUtils.stream;
import static com.github.gv2011.util.CollectionUtils.toISortedMap;
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Function;

import org.slf4j.Logger;

import com.github.gv2011.util.ReflectionUtils;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.beans.BeanBuilder;
import com.github.gv2011.util.beans.BeanType;
import com.github.gv2011.util.beans.DefaultValue;
import com.github.gv2011.util.beans.FixedValue;
import com.github.gv2011.util.beans.Partial;
import com.github.gv2011.util.beans.Property;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;
import com.github.gv2011.util.json.JsonObject;


class DefaultBeanType<T> extends AbstractType<T> implements BeanType<T> {

  private static final Logger LOG = getLogger(DefaultBeanType.class);

  private static final ISet<String> RESERVED = setOf();

  @SuppressWarnings("rawtypes")
  private static final Partial EMPTY_PARTIAL = createEmptyPartial();

  final DefaultTypeRegistry registry;

  //recursion, init later
  private @Nullable ISortedMap<String, PropertyImp<?>> properties;
  //recursion, init later
  private @Nullable Optional<T> defaultValue;
  //recursion, init later
  //private @Nullable Optional<PolymorphicInfo<T>> polymorphicInfo;


  DefaultBeanType(final Class<T> beanClass, final DefaultTypeRegistry registry) {
    super(registry.jf, beanClass);
    this.registry = registry;
    verify(beanClass.isInterface(), beanClass::toString);
  }


  @Override
  final void initialize() {
    if(properties==null) {
      LOG.debug("Initializing {}.", this);
      verify(defaultValue==null);
      properties = createProperties();
      defaultValue = createDefaultValue();
      LOG.debug("{} initialized.", this);
    }
  }


  private Optional<T> createDefaultValue() {
    LOG.debug("Creating default value for {}.", this);
    if(properties().values().stream()
      .allMatch(p->{
        Optional<?> defaultValue;
        try {
          defaultValue = p.defaultValue();
        } catch (final Exception e) {
          throw new IllegalStateException(format("Could not get default value of {} of {}.", p, this), e);
        }
        return defaultValue.isPresent();
      })
    ){
      final BeanBuilder<T> b = createBuilder();
      for(final PropertyImp<?> p: properties().values()) setDefaultValue(b, p);
      return Optional.of(b.build());
    }
    else return Optional.empty();
  }

  private <V> void setDefaultValue(final BeanBuilder<T> b, final PropertyImp<V> p) {
    setValue(b, p, p.defaultValue().get());
  }


  @Override
  public final BeanBuilder<T> createBuilder() {
      return new BeanBuilderImp<>(this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public final Partial<T> emptyPartial() {
      return EMPTY_PARTIAL;
  }


  @Override
  public final ISortedMap<String, PropertyImp<?>> properties() {
      initialize();
      return notNull(properties);
  }

  private ISortedMap<String, PropertyImp<?>> createProperties() {
    return Arrays.stream(clazz.getMethods())
    .filter(DefaultBeanType::isPropertyMethod)
    .map(this::createProperty)
    .collect(toISortedMap(
        p->p.name(),
        p->p
    ));
  }

  private static boolean isPropertyMethod(final Method m) {
      return m.getParameterCount()>0 ? false : ! RESERVED.contains(m.getName());
  }

  private <V> PropertyImp<V> createProperty(final Method m) {
    @SuppressWarnings("unchecked")
    final AbstractType<V> type = (AbstractType<V>) registry.type(m.getGenericReturnType());
    return createProperty(m, type);
  }

  <V> PropertyImp<V> createProperty(final Method m, final AbstractType<V> type) {
    final Optional<V> defaultValue =
      Optional.ofNullable(m.getAnnotation(DefaultValue.class))
      .map(a->type.parse(parseTolerant(type, registry.jf, a.value())))
    ;
    final Optional<V> fixedValue =
      Optional.ofNullable(m.getAnnotation(FixedValue.class))
      .map(a->type.parse(parseTolerant(type, registry.jf, a.value())))
    ;
    verify(!(defaultValue.isPresent() && fixedValue.isPresent()));
    return new PropertyImp<>(m.getName(), type, defaultValue, fixedValue);
  }


  static final JsonNode parseTolerant(final AbstractType<?> type, final JsonFactory jf, final String string) {
    try {
      JsonNode result;
          if(type.isOptional()) {
              if(string.trim().equals("null")) result = jf.jsonNull();
              else {
                  final CollectionType<?,?,?> cType = (CollectionType<?,?,?>) type;
                  result = parseTolerant(cType.elementType(), jf, string);
              }
          }
          else if(type instanceof AbstractElementaryType) {
            final AbstractElementaryType<?> eType = (AbstractElementaryType<?>) type;
            if(eType.jsonNodeType().equals(JsonNodeType.STRING)) {
              final String trimmed = string.trim();
              if(trimmed.isEmpty()?true:trimmed.charAt(0)!='"') {
                  result = jf.primitive(string);
              }
              else result = jf.deserialize(string);
            }
            else result = jf.deserialize(string);
        }
        else result = jf.deserialize(string);
        return result;
    } catch (final Exception e) {
      throw new IllegalArgumentException(format(
          "Could not parse annotated default value \"{}\" to type {}.",
          string, type
      ));
    }
  }

  @Override
  public final T parse(final JsonNode json) {
    final JsonObject obj = (JsonObject) json;
    verifyJsonHasNoAdditionalProperties(obj);
    final BeanBuilder<T> b = createBuilder();
    for(final PropertyImp<?> p: properties().values()) {
      final Optional<JsonNode> childNode = obj.tryGet(p.name());
      if(childNode.isPresent()) {
        final JsonNode value = childNode.get();
        try {
          setJsonValue(b, p, value);
        }
        catch (final RuntimeException e) { throw new IllegalArgumentException(
          format("Could not set property {} from JSON {}.", p, value), e);
        }
      }
    }
    return b.build();
  }

  private void verifyJsonHasNoAdditionalProperties(final JsonObject obj) {
    final TreeSet<String> additional = new TreeSet<>(obj.keySet());
    additional.removeAll(properties().keySet());
    verify(
      additional.isEmpty(),
      ()->format(
        "{}: Cannot parse JSON because of additional properties: {}", this, additional
      )
    );
  }


  private <V> void setJsonValue(final BeanBuilder<T> b, final Property<V> p, final JsonNode json) {
    setValue(b, p, p.type().parse(json));
  }

  private <V> void setValue(final BeanBuilder<T> b, final Property<V> p, final V value) {
    b.set(p, value);
  }

  @Override
  public final JsonObject toJson(final T object) {
    return properties().values().stream()
      .flatMap(p->stream(toJson(object, p).map(j->pair(p.name(), j))))
      .collect(jf.toJsonObject())
    ;
  }

  @Override
  public final boolean isDefault(final T obj) {
    return properties().values().stream().allMatch(p->isDefault(obj, p));
  }



  @Override
  public final Optional<T> getDefault() {
    initialize();
    return notNull(defaultValue, ()->clazz.toString());
  }


  private <V> boolean isDefault(final T bean, final PropertyImp<V> property) {
    return property.type().isDefault(get(bean, property));
  }

  private <V> Optional<JsonNode> toJson(final T bean, final PropertyImp<V> property) {
    final V value = get(bean, property);
    final AbstractType<V> type = property.type();
    if(type.isDefault(value)) return Optional.empty();
    else return Optional.of(type.toJson(value));
  }

  @Override
  public final <V> V get(final T bean, final Property<V> property) {
    return property.type().cast(call(()->clazz.getMethod(property.name()).invoke(bean)));
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

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public final <V> PropertyImp<V> getProperty(final Function<T, V> method) {
    final Method m = ReflectionUtils.method(clazz, method);
    final PropertyImp<?> result = properties().get(m.getName());
    verifyEqual(result.type().clazz, m.getReturnType());
    return (PropertyImp)result;
  }

}
