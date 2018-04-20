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
import static com.github.gv2011.util.CollectionUtils.listBuilder;
import static com.github.gv2011.util.CollectionUtils.setOf;
import static com.github.gv2011.util.CollectionUtils.single;
import static com.github.gv2011.util.CollectionUtils.stream;
import static com.github.gv2011.util.CollectionUtils.toISet;
import static com.github.gv2011.util.CollectionUtils.toOpt;
import static com.github.gv2011.util.Nothing.nothing;
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.ex.Exceptions.format;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.github.gv2011.util.ServiceLoaderUtils;
import com.github.gv2011.util.ann.VisibleForTesting;
import com.github.gv2011.util.beans.AnnotationHandler;
import com.github.gv2011.util.beans.ElementaryTypeHandler;
import com.github.gv2011.util.beans.ElementaryTypeHandlerFactory;
import com.github.gv2011.util.beans.Type;
import com.github.gv2011.util.beans.TypeRegistry;
import com.github.gv2011.util.beans.imp.DefaultBeanFactory.DefaultBeanFactoryBuilder;
import com.github.gv2011.util.cache.CacheUtils;
import com.github.gv2011.util.cache.SoftIndex;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.tstr.TypedString;

public class DefaultTypeRegistry implements TypeRegistry{

  private static final Class<?> OPT = Opt.class;
  private static final Class<?> OPTIONAL = Optional.class;
  private static final Class<?> ILIST = IList.class;
  private static final Class<?> ISET = ISet.class;
  private static final Class<?> ISORTEDSET = ISortedSet.class;
  private static final Class<?> IMAP = IMap.class;
  private static final Class<?> ISORTEDMAP = ISortedMap.class;

  static final ISet<Class<?>> COLLECTION_CLASSES = setOf(
    OPT, ILIST, ISET, ISORTEDSET, IMAP, ISORTEDMAP
  );

  private static final Logger LOG = getLogger(DefaultTypeRegistry.class);

  private final JsonFactory jf;

  @VisibleForTesting
  final BeanFactory beanFactory;

  private final SoftIndex<Class<?>,TypeSupport<?>> typeMap = createTypeMap();

  private SoftIndex<Class<?>, TypeSupport<?>> createTypeMap() {
    return CacheUtils.softIndex(
      c->tryCreateType(c),
      p->p.getValue().ifPresent(TypeSupport::initialize).orElse(nothing())
    );
  }

  private final DefaultElementaryTypeHandlerFactory defaultFactory = new DefaultElementaryTypeHandlerFactory();
  private final IList<ElementaryTypeHandlerFactory> additionalTypeHandlerFactories;

  final AnnotationHandler annotationHandler = new DefaultAnnotationHandler();

  public DefaultTypeRegistry() {
    this(ServiceLoaderUtils.loadService(JsonFactory.class));
  }

  public DefaultTypeRegistry(final JsonFactory jsonFactory) {
      this(
          jsonFactory,
          ServiceLoaderUtils.tryGetService(BeanFactoryBuilder.class).orElseGet(DefaultBeanFactoryBuilder::new)
      );
    }

  public DefaultTypeRegistry(final BeanFactoryBuilder bfb) {
      this(ServiceLoaderUtils.loadService(JsonFactory.class));
    }

  public DefaultTypeRegistry(final JsonFactory jsonFactory, final BeanFactoryBuilder bfb) {
    jf = jsonFactory;
    beanFactory = bfb.build(jf, annotationHandler, this);
    final IList.Builder<ElementaryTypeHandlerFactory> b = listBuilder();
    for(final ElementaryTypeHandlerFactory tf: ServiceLoader.load(ElementaryTypeHandlerFactory.class)) {
      b.add(tf);
    }
    additionalTypeHandlerFactories = b.build();
    LOG.info("additionalTypeHandlerFactories:{}", additionalTypeHandlerFactories);
  }

  @Override
  public <T> BeanTypeSupport<T> beanType(final Class<T> beanClass) {
    try {
      return (BeanTypeSupport<T>) type(beanClass);
    } catch (final RuntimeException e) {
      final String reason = beanFactory.notBeanReason(beanClass);
      throw new IllegalArgumentException(format("{}---: {}", beanClass, reason), e);
    }
  }

  <T> AbstractPolymorphicSupport<T> abstractBeanType(final Class<T> abstractBeanClass) {
    return (AbstractPolymorphicSupport<T>) type(abstractBeanClass);
  }

  public <E> AbstractElementaryType<E> elementaryType(final Class<E> elementaryClass) {
    return (AbstractElementaryType<E>) type(elementaryClass);
  }

  @SuppressWarnings("unchecked")
  public <T> TypeSupport<T> type(final Class<T> clazz) {
    return (TypeSupport<T>) typeMap.tryGet(clazz)
      .orElseThrow(()->new NoSuchElementException(format("{} is not supported.", clazz)))
    ;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  <T> TypeSupport<T> type(final java.lang.reflect.Type genType) {
    if(genType instanceof Class) return type((Class)genType);
    else if(genType instanceof ParameterizedType) {
      final ParameterizedType pType = (ParameterizedType)genType;
      final Class rawType = (Class) pType.getRawType();
      if(rawType.equals(ILIST)) {
        return
          (TypeSupport<T>) type(filterWildCard(single(pType.getActualTypeArguments())))
          .collectionType(Structure.list())
        ;
      }
      else if(rawType.equals(OPT)) {
        return
          (TypeSupport<T>) type(filterWildCard(single(pType.getActualTypeArguments())))
          .collectionType(Structure.opt())
        ;
      }
      else if(rawType.equals(OPTIONAL)) {
        return
          (TypeSupport<T>) type(filterWildCard(single(pType.getActualTypeArguments())))
          .collectionType(Structure.optional())
        ;
      }
      else if(rawType.equals(ISET)) {
        return
          (TypeSupport<T>) type(filterWildCard(single(pType.getActualTypeArguments())))
          .collectionType(Structure.set())
        ;
      }
      else if(rawType.equals(ISORTEDSET)) {
        return
          (TypeSupport<T>) type(filterWildCard(single(pType.getActualTypeArguments())))
          .collectionType(Structure.sortedSet())
        ;
      }
      else if(rawType.equals(IMAP)||rawType.equals(ISORTEDMAP)) {
        assert pType.getActualTypeArguments().length==2;
        final TypeSupport keyType = type(filterWildCard(pType.getActualTypeArguments()[0]));
        final TypeSupport valueType = type(filterWildCard(pType.getActualTypeArguments()[1]));
        if(keyType.hasStringForm()) {
          return keyType.mapType(Structure.stringMap(), valueType);
        }
        else return keyType.mapType(Structure.map(), valueType);
      }
      else throw new UnsupportedOperationException();
    }
    else throw new UnsupportedOperationException(genType.toString());
  }

  private java.lang.reflect.Type filterWildCard(final java.lang.reflect.Type genType){
    if(genType instanceof WildcardType){
      final WildcardType wc = (WildcardType)genType;
      if(wc.getLowerBounds().length>0) throw new UnsupportedOperationException(genType.toString());
      final java.lang.reflect.Type[] upper = wc.getUpperBounds();
      if(upper.length!=1) throw new UnsupportedOperationException(genType.toString());
      return notNull(upper[0]);
    }
    else return genType;
  }

  @SuppressWarnings("unchecked")
  private <T> Opt<TypeSupport<T>> tryCreateType(final Class<T> clazz) {
    Opt<TypeSupport<T>> result;
    LOG.debug("Creating type for {}.", clazz);
    if(isCollectionType(clazz)) result = Opt.empty();
    else if(isTypedStringType(clazz)) result = Opt.of(createTypedStringType(clazz));
    else{
      final Opt<TypeSupport<T>> beanType = beanFactory.tryCreate(clazz).map(t->t);
      if(beanType.isPresent()) result = beanType;
      else result = tryCreateElementaryType(clazz).map(t->t);
    }
    if(result.isPresent()) LOG.debug("Created {} for {}.", result.get(), clazz);
    else LOG.info("{} is not supported.", clazz);
    return result;
  }

  private boolean isTypedStringType(final Class<?> clazz) {
    return TypedString.class.isAssignableFrom(clazz);
  }

  private boolean isCollectionType(final Class<?> clazz) {
    return COLLECTION_CLASSES.contains(clazz);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private TypedStringType createTypedStringType(final Class<?> clazz) {
    return new TypedStringType(jf, clazz);
  }


  private <T> Opt<ElementaryTypeImp<T>> tryCreateElementaryType(final Class<T> clazz) {
    Opt<ElementaryTypeHandler<T>> typeHandler = additionalTypeHandlerFactories.parallelStream()
      .flatMap(f->f.isSupported(clazz) ? Stream.of(f.getTypeHandler(clazz)) : Stream.empty())
      .collect(toOpt())
    ;
    if(!typeHandler.isPresent()) typeHandler = defaultFactory.tryGetTypeHandler(clazz);
    return typeHandler.map(th->new ElementaryTypeImp<>(jf,clazz,th));
  }


  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Type<?> getTypeOfObject(final Object beanOrElementary){
    if(beanOrElementary instanceof Collection) throw new IllegalArgumentException();
    else if(beanOrElementary instanceof Optional) throw new IllegalArgumentException();
    else {
      final Class clazz = beanOrElementary.getClass();
      if(clazz.getTypeParameters().length!=0) throw new IllegalArgumentException();
      return beanFactory.tryGetBeanInterface(clazz)
        .map(i->(Type)beanType(i))
        .orElseGet(()->type(clazz))
      ;
    }
  }


  @Override
  public <S extends TypedString<S>> S typedString(final Class<S> clazz, final String value) {
    return ((TypedStringType<S>)type(clazz)).create(value);
  }

  @Override
  public boolean isSupported(final Class<?> clazz) {
    return typeMap.getIfPresent(clazz)
      .map(Opt::isPresent) //if there is information in the cache, use it
      .orElseGet(()->{
        return notSupportedReason(clazz).isEmpty();
      }
    );
  }

  private boolean supported2(final Class<?> clazz) {
    return isCollectionType(clazz) ||
    beanFactory.isSupported(clazz) ||
    defaultFactory.isSupported(clazz) ||
    additionalTypeHandlerFactories.parallelStream().anyMatch(f->f.isSupported(clazz));
  }

  public String notSupportedReason(final Class<?> clazz) {
    final String result;
    if(isCollectionType(clazz)) result = "";
    else{
      final String reason = beanFactory.notSupportedReason(clazz);
      if(!reason.isEmpty()){
        if(
          defaultFactory.isSupported(clazz) ||
          additionalTypeHandlerFactories.parallelStream().anyMatch(f->f.isSupported(clazz))
        ){
          result = "";
        }
        else result = reason;
      }
      else result = reason;
    }
    assert result.isEmpty() == supported2(clazz);
    return result;
  }

  <T> Opt<TypeSupport<T>> getTypeIfCached(final Class<T> clazz){
    return
      typeMap.getIfPresent(clazz).orElse(Opt.empty())
      .map(t->t.castTo(clazz))
    ;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <T> Opt<TypeSupport<? super T>> findTypeForInstanceClass(final Class<T> instanceClass) {
    Opt<TypeSupport<?>> result = Opt.empty();
    if(isSupported(instanceClass)) result = Opt.of(type(instanceClass));
    if(!result.isPresent()) {
      final Opt<Class<?>> sup = Opt.ofNullable(instanceClass.getSuperclass());
      if(sup.isPresent()) {
        if(isSupported(sup.get())) result = Opt.of(type(sup.get()));
      }
    }
    if(!result.isPresent()) {
      final Class<?>[] interfaces = instanceClass.getInterfaces();
      final ISet<Class<?>> types = stream(interfaces)
        .filter(i->isSupported(i))
        .collect(toISet())
      ;
      if(types.size()==1) result = Opt.of((TypeSupport<? super T>) type(single(types)));
      ;
    }
    return (Opt<TypeSupport<? super T>>)(Opt)result;
  }

  final JsonFactory jf() {
    return jf;
  }

}
