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
import static com.github.gv2011.util.CollectionUtils.toOptional;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.github.gv2011.util.XStream;
import com.github.gv2011.util.beans.BeanType;
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
import com.github.gv2011.util.json.JsonUtils;

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

  final JsonFactory jf = JsonUtils.jsonFactory();

  private final SoftIndex<Class<?>,AbstractType<?>> typeMap = CacheUtils.softIndex(c->Optional.of(createType(c)));

  private final DefaultElementaryTypeHandlerFactory defaultFactory = new DefaultElementaryTypeHandlerFactory();
  private final IList<ElementaryTypeHandlerFactory> additionalTypeHandlerFactories;

  private final AbstractType<String> stringType;

  public DefaultTypeRegistry() {
      final IList.Builder<ElementaryTypeHandlerFactory> b = listBuilder();
      for(final ElementaryTypeHandlerFactory tf: ServiceLoader.load(ElementaryTypeHandlerFactory.class)) {
          b.add(tf);
      }
      additionalTypeHandlerFactories = b.build();
      stringType = type(String.class);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> BeanType<T> beanType(final Class<T> beanClass) {
      return (BeanType<T>) type(beanClass);
  }

  @SuppressWarnings("unchecked")
  <T> AbstractType<T> type(final Class<T> clazz) {
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
            final AbstractType keyType = (AbstractType) type(pType.getActualTypeArguments()[0]);
            final AbstractType valueType = (AbstractType) type(pType.getActualTypeArguments()[1]);
            if(keyType.equals(stringType)) {
              return keyType.mapType(Structure.stringMap(), valueType);
            }
            else return keyType.mapType(Structure.map(), valueType);
          }
          else throw new UnsupportedOperationException();
      }
      else throw new UnsupportedOperationException();
  }

  private <T> AbstractType<T> createType(final Class<T> clazz) {
    assert !ILIST.isAssignableFrom(clazz);
    assert !Map.class.isAssignableFrom(clazz);
    assert !OPTIONAL.equals(clazz);
    if(isElementaryType(clazz)) return createElementaryType(clazz);
    else return (AbstractType<T>)createBeanType(clazz);
  }

  private <T> BeanTypeImp<T> createBeanType(final Class<T> clazz) {
    return new BeanTypeImp<>(clazz, this);
  }

  private boolean isElementaryType(final Class<?> clazz) {
    return !COLLECTION_CLASSES.contains(clazz) && !isBeanClass(clazz);
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
    boolean result;
    if(!clazz.isInterface()) result = false;
    else if(clazz.getTypeParameters().length!=0) result = false;
    else if(Arrays.stream(clazz.getInterfaces()).anyMatch(this::isBeanClass)) result = false;
    else result = Arrays.stream(clazz.getMethods()).filter(this::isPropertyMethod).findAny().isPresent();
    LOG.debug("{} is {}a bean class.", clazz, result?"":"not ");
    return result;
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
        .orElseGet(()->(Type)type(clazz))
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


}
