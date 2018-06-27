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

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.toISortedMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.ReflectionUtils;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.beans.AnnotationHandler;
import com.github.gv2011.util.beans.BeanBuilder;
import com.github.gv2011.util.beans.BeanType;
import com.github.gv2011.util.beans.Partial;
import com.github.gv2011.util.beans.Property;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;
import com.github.gv2011.util.json.JsonNodeType;
import com.github.gv2011.util.json.JsonObject;


public abstract class BeanTypeSupport<T> extends ObjectTypeSupport<T> implements BeanType<T> {

  private static final Logger LOG = getLogger(BeanTypeSupport.class);

  @SuppressWarnings("rawtypes")
  private static final Partial EMPTY_PARTIAL = createEmptyPartial();

  private final JsonFactory jf;
  final AnnotationHandler annotationHandler;
  //private final Function<Type,AbstractType<?>> registry;
  private final BeanFactory beanFactory;

  //recursion, init later
  private @Nullable ISortedMap<String, PropertyImp<T,?>> properties;
  //recursion, init later
  private @Nullable Opt<T> defaultValue;

  protected BeanTypeSupport(
    final Class<T> beanClass,
    final JsonFactory jf,
    final AnnotationHandler annotationHandler,
    final BeanFactory beanFactory
  ) {
    super(beanClass);
    this.jf = jf;
    this.annotationHandler = annotationHandler;
    this.beanFactory = beanFactory;
  }

  @Override
  final JsonFactory jf() {
    return jf;
  }

  private final Function<Type,TypeSupport<?>> registry(){
    return beanFactory.registry();
  }

  @Override
  final void initialize() {
    if(!isInitialized()) {
      verify(properties==null);
      verify(defaultValue==null);
      LOG.debug("{}: initializing properties.", this);
      properties = createProperties();
      checkProperties(properties);
      LOG.debug("{}: initialized properties: {}.", this, properties.keySet());
      LOG.debug("{}: initializing default value.", this);
      defaultValue = createDefaultValue(properties);
      LOG.debug("{} initialized default value.", this);
    }
    else{
      verify(properties!=null);
      verify(defaultValue!=null);
    }
  }

  @Override
  protected final boolean isInitialized() {
    return defaultValue!=null;
  }

  @Override
  public final int hashCode(final T bean) {
    return clazz.hashCode() * 31 + properties.values().stream()
      .mapToInt(p->p.name().hashCode() ^ p.getValue(bean).hashCode())
      .sum()
    ;
  }

  final ISortedMap<String, Object> getValues(final T bean) {
    return properties().values().stream()
      .flatMap(p->{
        Stream<Pair<String,Object>> result;
        final Object value = p.getValue(bean);
        if(p.defaultValue().map(dv->dv.equals(value)).orElse(false)){
          result = XStream.empty(); //remove default values
        }
        else result = XStream.of(pair(p.name(),value));
        return result;
      })
      .collect(toISortedMap())
    ;
  }

  void checkProperties(final ISortedMap<String, PropertyImp<T,?>> properties) {}

  private Opt<T> createDefaultValue(final ISortedMap<String, PropertyImp<T,?>> properties) {
    LOG.debug("Creating default value for {}.", this);
    if(properties.values().stream()
      .allMatch(p->{
        Opt<?> defaultValue;
        try {
          defaultValue = p.defaultValue();
        } catch (final Exception e) {
          throw new IllegalStateException(format("Could not get default value of {} of {}.", p, this), e);
        }
        return defaultValue.isPresent();
      })
    ){
      final BeanBuilder<T> b = createBuilder();
      return Opt.of(b.build());
    }
    else return Opt.empty();
  }

  @SuppressWarnings("unchecked")
  @Override
  public final Partial<T> emptyPartial() {
      return EMPTY_PARTIAL;
  }

  @Override
  public final ISortedMap<String, PropertyImp<T,?>> properties() {
      if(properties==null) initialize();
      return notNull(properties);
  }

  private ISortedMap<String, PropertyImp<T,?>> createProperties() {
    return Arrays.stream(clazz.getMethods())
    .filter(beanFactory::isPropertyMethod)
    .map(this::createProperty)
    .collect(toISortedMap(
        p->p.name(),
        p->p
    ));
  }

//  private static boolean isPropertyMethod(final Method m) {
//      return m.getParameterCount()>0 ? false : ! RESERVED.contains(m.getName());
//  }

  private <V> PropertyImp<T,V> createProperty(final Method m) {
    try {
      @SuppressWarnings("unchecked")
      final TypeSupport<V> type = (TypeSupport<V>) registry().apply(m.getGenericReturnType());
      return createProperty(m, type);
    } catch (final RuntimeException e) {
      throw new RuntimeException(format("{}: Could not create property for method {}.", this, m), e);
    }
  }

  <V> PropertyImp<T,V> createProperty(final Method m, final TypeSupport<V> type) {
    final Opt<V> fixedValue =
      annotationHandler.fixedValue(m)
      .map(v->type.parse(parseTolerant(type, jf, v)))
    ;
    final Opt<V> annotatedDefaultValue =
      annotationHandler.defaultValue(m)
      .map(v->type.parse(parseTolerant(type, jf, v)))
    ;
    if(fixedValue.isPresent()) {
      if(annotatedDefaultValue.isPresent()) verifyEqual(annotatedDefaultValue, fixedValue);
      return PropertyImp.createFixed(this, m, m.getName(), type, fixedValue.get());
    }
    else{
      final Opt<V> defaultValue = annotatedDefaultValue
        .or(()->type.isInitialized() ? type.getDefault() : Opt.empty())
      ;
      return PropertyImp.create(this, m, type, defaultValue);
    }
  }

  static final JsonNode parseTolerant(final TypeSupport<?> type, final JsonFactory jf, final String string) {
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
    for(final PropertyImp<T,?> p: properties().values()) {
      final Opt<JsonNode> childNode = obj.tryGet(p.name());
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
      .flatOpt(p->toJson(object, p).map(j->pair(p.name(), j)))
      .collect(jf.toJsonObject())
    ;
  }

  @Override
  public final boolean isDefault(final T obj) {
    return properties().values().stream().allMatch(p->isDefault(obj, p));
  }



  @Override
  public final Opt<T> getDefault() {
    initialize();
    return notNull(defaultValue, ()->clazz.toString());
  }


  private <V> boolean isDefault(final T bean, final PropertyImp<T,V> property) {
    return property.type().isDefault(get(bean, property));
  }

  private <V> Opt<JsonNode> toJson(final T bean, final PropertyImp<T,V> property) {
    final V value = get(bean, property);
    final TypeSupport<V> type = property.type();
    if(type.isDefault(value)) return Opt.empty();
    else return Opt.of(type.toJson(value));
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
  public final <V> PropertyImp<T,V> getProperty(final Function<T, V> method) {
    final Method m = getMethod(method);
    final PropertyImp<T,?> result = properties().get(m.getName());
    verifyEqual(result.type().clazz, m.getReturnType());
    return (PropertyImp)result;
  }


  protected <V> Method getMethod(final Function<T, V> method) {
    return ReflectionUtils.method(clazz, method);
  }


}
