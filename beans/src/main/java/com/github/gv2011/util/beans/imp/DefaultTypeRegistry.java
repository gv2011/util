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

import com.github.gv2011.util.ServiceLoaderUtils;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.beans.Abstract;
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

  private final SoftIndex<Class<?>,AbstractType<?>> typeMap = CacheUtils.softIndex(
      c->Optional.of(createType(c)),
      p->p.getValue().ifPresent(AbstractType::initialize)
  );

  private final DefaultElementaryTypeHandlerFactory defaultFactory = new DefaultElementaryTypeHandlerFactory();
  private final IList<ElementaryTypeHandlerFactory> additionalTypeHandlerFactories;

  private final AbstractType<String> stringType;

  public DefaultTypeRegistry() {
      this(ServiceLoaderUtils.loadService(JsonFactory.class));
  }

  public DefaultTypeRegistry(final JsonFactory jsonFactory) {
      this.jf = jsonFactory;
      final IList.Builder<ElementaryTypeHandlerFactory> b = listBuilder();
      for(final ElementaryTypeHandlerFactory tf: ServiceLoader.load(ElementaryTypeHandlerFactory.class)) {
          b.add(tf);
      }
      additionalTypeHandlerFactories = b.build();
      stringType = type(String.class);
  }

  @Override
  public <T> BeanTypeImp<T> beanType(final Class<T> beanClass) {
      return (BeanTypeImp<T>) type(beanClass);
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
      else throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unchecked")
  private <T> AbstractType<T> createType(final Class<T> clazz) {
    if(isCollectionType(clazz)) throw new UnsupportedOperationException();
    else if(isTypedStringType(clazz)) return createTypedStringType(clazz);
    else if(isBeanClass(clazz)) return createBeanType(clazz);
    else return createElementaryType(clazz);
  }

  private <T> BeanTypeImp<T> createBeanType(final Class<T> clazz) {
    return new BeanTypeImp<>(clazz, this);
  }

  private boolean isTypedStringType(final Class<?> clazz) {
    return TypedString.class.isAssignableFrom(clazz);
  }

  private boolean isCollectionType(final Class<?> clazz) {
    return COLLECTION_CLASSES.contains(clazz);
  }

  @SuppressWarnings("unused")
  private boolean isElementaryType(final Class<?> clazz) {
    return
      !isCollectionType(clazz) &&
      !isTypedStringType(clazz) &&
      !isBeanClass(clazz)
    ;
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

  private boolean isBeanClass(final Class<?> clazz) {
    final String notBeanReason;
    if(!clazz.isInterface())
        notBeanReason = "not an interface";
    else if(clazz.getTypeParameters().length!=0)
        notBeanReason = "parameterized class";
    else if(clazz.getAnnotation(Abstract.class)!=null)
        notBeanReason = "annotated as abstract";
    else if(Arrays.stream(clazz.getInterfaces()).anyMatch(this::isBeanClass))
        notBeanReason = "is subclass of a bean class";
    else if(!Arrays.stream(clazz.getMethods()).filter(this::isPropertyMethod).findAny().isPresent())
        notBeanReason = "has no properties";
    else {
        notBeanReason = "";
    }
    if(notBeanReason.isEmpty()) {
        LOG.trace("{} is a bean class.", clazz);
        return true;
    }
    else {
        LOG.trace("{} is not a bean class ({}).", clazz, notBeanReason);
        return false;
    }
  }

  private boolean isPropertyMethod(final Method m) {
    assert m.getDeclaringClass().isInterface();
    return m.getParameterCount()==0;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Type<?> getType(final Object beanOrElementary){
    if(beanOrElementary instanceof IList) throw new IllegalArgumentException();
    else {
      final Class clazz = beanOrElementary.getClass();
      if(clazz.getTypeParameters().length!=0) throw new IllegalArgumentException();
      return getBeanInterface(clazz)
        .map(i->(Type)beanType(i))
        .orElseGet(()->type(clazz))
      ;
    }
  }

  public Optional<Class<?>> getBeanInterface(final Class<?> clazz){
    if(clazz.getTypeParameters().length!=0) return Optional.empty();
    else {
      final Optional<Class<?>> fromSuper = Optional.ofNullable(clazz.getSuperclass()).flatMap(this::getBeanInterface);
      final Optional<Class<?>> fromSuperInterfaces = XStream.of(clazz.getInterfaces())
        .flatOptional(i->getBeanInterface(i))
        .toOptional()
      ;
      final Optional<Class<?>> combined =
        XStream.fromOptional(fromSuper).concat(XStream.fromOptional(fromSuperInterfaces)).toOptional()
      ;
      if(combined.isPresent()) {
        assert(!isBeanClass(clazz));
        return combined;
      }
      else {
        return isBeanClass(clazz) ? Optional.of(clazz) : Optional.empty();
      }
    }
  }

  @Override
  public <S extends TypedString<S>> S typedString(final Class<S> clazz, final String value) {
    return ((TypedStringType<S>)type(clazz)).create(value);
  }

  @Override
  public boolean isSupported(final Class<?> clazz) {
    return
      isCollectionType(clazz) ||
      isBeanClass(clazz) ||
      defaultFactory.isSupported(clazz) ||
      additionalTypeHandlerFactories.parallelStream().anyMatch(f->f.isSupported(clazz))
    ;
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
