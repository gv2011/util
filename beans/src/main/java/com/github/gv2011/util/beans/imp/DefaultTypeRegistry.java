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
import static com.github.gv2011.util.CollectionUtils.toOptional;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.github.gv2011.util.ReflectionUtils;
import com.github.gv2011.util.ServiceLoaderUtils;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.beans.AnnotationHandler;
import com.github.gv2011.util.beans.ElementaryTypeHandlerFactory;
import com.github.gv2011.util.beans.Type;
import com.github.gv2011.util.beans.TypeRegistry;
import com.github.gv2011.util.cache.CacheUtils;
import com.github.gv2011.util.cache.SoftIndex;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.tstr.TypedString;

public class DefaultTypeRegistry implements TypeRegistry{

  private static final Class<?> OPTIONAL = Optional.class;
  private static final Class<?> ILIST = IList.class;
  private static final Class<?> ISET = ISet.class;
  private static final Class<?> ISORTEDSET = ISortedSet.class;
  private static final Class<?> IMAP = IMap.class;
  private static final Class<?> ISORTEDMAP = ISortedMap.class;

  static final ISet<Class<?>> COLLECTION_CLASSES = setOf(
    OPTIONAL, ILIST, ISET, ISORTEDSET, IMAP, ISORTEDMAP
  );

  private static final Logger LOG = getLogger(DefaultTypeRegistry.class);

  final JsonFactory jf;
  final BeanFactory beanFactory = new BeanFactory();

  private final SoftIndex<Class<?>,AbstractType<?>> typeMap = createTypeMap();

  private SoftIndex<Class<?>, AbstractType<?>> createTypeMap() {
    return CacheUtils.softIndex(
      c->Optional.of(createType(c)), //TODO: cache info about not supported classes
      p->p.getValue().ifPresent(AbstractType::initialize)
    );
  }

  private final DefaultElementaryTypeHandlerFactory defaultFactory = new DefaultElementaryTypeHandlerFactory();
  private final IList<ElementaryTypeHandlerFactory> additionalTypeHandlerFactories;

  private final AbstractType<String> stringType;

  final AnnotationHandler annotationHandler = new DefaultAnnotationHandler();

  public DefaultTypeRegistry() {
    this(ServiceLoaderUtils.loadService(JsonFactory.class));
  }

  public DefaultTypeRegistry(final JsonFactory jsonFactory) {
    jf = jsonFactory;
    final IList.Builder<ElementaryTypeHandlerFactory> b = listBuilder();
    for(final ElementaryTypeHandlerFactory tf: ServiceLoader.load(ElementaryTypeHandlerFactory.class)) {
      b.add(tf);
    }
    additionalTypeHandlerFactories = b.build();
    stringType = type(String.class);
  }

  @Override
  public <T> DefaultBeanType<T> beanType(final Class<T> beanClass) {
    return (DefaultBeanType<T>) type(beanClass);
  }

  <T> AbstractPolymorphicBeanSupport<T> abstractBeanType(final Class<T> abstractBeanClass) {
    return (AbstractPolymorphicBeanSupport<T>) type(abstractBeanClass);
  }

  public <E> AbstractElementaryType<E> elementaryType(final Class<E> elementaryClass) {
    return (AbstractElementaryType<E>) type(elementaryClass);
  }

  @SuppressWarnings("unchecked")
  public <T> AbstractType<T> type(final Class<T> clazz) {
    return (AbstractType<T>) typeMap.get(clazz);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  <T> AbstractType<T> type(final java.lang.reflect.Type genType) {
    if(genType instanceof Class) return type((Class)genType);
    else if(genType instanceof ParameterizedType) {
      final ParameterizedType pType = (ParameterizedType)genType;
      final Class rawType = (Class) pType.getRawType();
      if(rawType.equals(ILIST)) {
        return
          (AbstractType<T>) type(single(pType.getActualTypeArguments()))
          .collectionType(Structure.list())
        ;
      }
      else if(rawType.equals(OPTIONAL)) {
        return
          (AbstractType<T>) type(single(pType.getActualTypeArguments()))
          .collectionType(Structure.opt())
        ;
      }
      else if(rawType.equals(ISET)) {
        return
          (AbstractType<T>) type(single(pType.getActualTypeArguments()))
          .collectionType(Structure.set())
        ;
      }
      else if(rawType.equals(ISORTEDSET)) {
        return
          (AbstractType<T>) type(single(pType.getActualTypeArguments()))
          .collectionType(Structure.sortedSet())
        ;
      }
      else if(rawType.equals(IMAP)||rawType.equals(ISORTEDMAP)) {
        assert pType.getActualTypeArguments().length==2;
        final AbstractType keyType = type(pType.getActualTypeArguments()[0]);
        final AbstractType valueType = type(pType.getActualTypeArguments()[1]);
        if(keyType.equals(stringType)) {
          return keyType.mapType(Structure.stringMap(), valueType);
        }
        else return keyType.mapType(Structure.map(), valueType);
      }
      else throw new UnsupportedOperationException();
    }
    else throw new UnsupportedOperationException(genType.toString());
  }

  @SuppressWarnings("unchecked")
  private <T> AbstractType<T> createType(final Class<T> clazz) {
    LOG.debug("Creating type for {}.", clazz);
    if(isCollectionType(clazz)) throw new UnsupportedOperationException();
    else if(isTypedStringType(clazz)) return createTypedStringType(clazz);
    else if(beanFactory.isBeanClass(clazz)) return beanFactory.createBeanType(clazz);
    else if(annotationHandler.isPolymorphicRoot(clazz)) return createAbstractBeanType(clazz);
    else return createElementaryType(clazz);
  }

  private <T> PolymorphicAbstractBeanRootType<T> createAbstractBeanType(final Class<T> clazz) {
    return new PolymorphicAbstractBeanRootType<>(this, clazz);
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


  private <T> ElementaryTypeImp<T> createElementaryType(final Class<T> clazz) {
    return new ElementaryTypeImp<>(
      jf,
      clazz,
      ( additionalTypeHandlerFactories.parallelStream()
        .flatMap(f->f.isSupported(clazz) ? Stream.of(f.getTypeHandler(clazz)) : Stream.empty())
        .collect(toOptional())
        .orElseGet(()->defaultFactory.getTypeHandler(clazz))
      )
    );
  }


  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Type<?> getType(final Object beanOrElementary){
    if(beanOrElementary instanceof IList) throw new IllegalArgumentException();
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
      .map(Optional::isPresent) //if there is information in the cache, use it
      .orElseGet(()->{
        return
          isCollectionType(clazz) ||
          beanFactory.isSupported(clazz) ||
           defaultFactory.isSupported(clazz) ||
          additionalTypeHandlerFactories.parallelStream().anyMatch(f->f.isSupported(clazz))
        ;
      }
    );
  }

  @SuppressWarnings("unchecked")
  public <T> Optional<AbstractType<? super T>> findTypeForInstanceClass(final Class<T> instanceClass) {
    if(isSupported(instanceClass)) return Optional.of(type(instanceClass));
    else {
        final ISet<Class<?>> types = stream(instanceClass.getInterfaces()).filter(i->isSupported(i)).collect(toISet());
        if(types.size()==1) return Optional.of((AbstractType<? super T>) type(single(types)));
        else return Optional.empty();
    }
  }

}
