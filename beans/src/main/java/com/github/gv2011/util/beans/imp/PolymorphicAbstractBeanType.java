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

import static com.github.gv2011.util.CollectionUtils.toIMap;
import static com.github.gv2011.util.CollectionUtils.toSingle;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;

import java.lang.reflect.Method;
import java.util.Optional;

import com.github.gv2011.util.ReflectionUtils;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.beans.BeanBuilder;
import com.github.gv2011.util.beans.Type;
import com.github.gv2011.util.beans.TypeResolver;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.json.JsonNode;

class PolymorphicAbstractBeanType<B> extends AbstractType<B>{

  static final String TYPE_PROPERTY = "type";

  private final TypeResolver<? super B> typeResolver;
  private final DefaultTypeRegistry typeRegistry;
  private final PolymorphicAbstractBeanType<? super B> rootType;

  @SuppressWarnings("unchecked")
  PolymorphicAbstractBeanType(final DefaultTypeRegistry typeRegistry, final Class<B> clazz) {
    super(typeRegistry.jf, clazz);
    this.typeRegistry = typeRegistry;
    //final Abstract annotation = notNull(clazz.getAnnotation(Abstract.class));
    checkTypeMethod(clazz, typeRegistry);
    final Optional<PolymorphicAbstractBeanType<? super B>> optRootType = rootType(typeRegistry,clazz);
    if(optRootType.isPresent()){
      rootType = optRootType.get();
      typeResolver = rootType.typeResolver;
    }
    else{
      rootType = this;
      @SuppressWarnings("rawtypes")
      final Optional<Class<? extends TypeResolver>> typeResolverClass =
        typeRegistry.annotationHandler.typeResolver(clazz)
      ;
      if(typeResolverClass.isPresent()) typeResolver = call(typeResolverClass.get()::newInstance);
      else typeResolver = defaultTypeResolver(typeRegistry.annotationHandler.subClasses(clazz));
    }
  }

  private void checkTypeMethod(final Class<B> clazz, final DefaultTypeRegistry typeRegistry) {
    final Method typeMethod = call(()->clazz.getMethod(TYPE_PROPERTY));
    final Type<?> typeType = typeRegistry.type(typeMethod.getReturnType());
    verify(typeType instanceof AbstractElementaryType);
  }

  @SuppressWarnings("unchecked")
  private Optional<PolymorphicAbstractBeanType<? super B>> rootType(
    final DefaultTypeRegistry typeRegistry, final Class<B> clazz
  ) {
    final Class<?> rootClass =
      ReflectionUtils.getAllInterfaces(clazz).stream().concat(XStream.of(clazz))
      .filter(typeRegistry.annotationHandler::isPolymorphicRoot)
      .collect(toSingle())
    ;
    if(rootClass.equals(clazz)) return Optional.empty();
    else return Optional.of((PolymorphicAbstractBeanType<? super B>)typeRegistry.abstractBeanType(rootClass));
  }

  private TypeResolver<B> defaultTypeResolver(final IList<Class<?>> classes) {
    final IMap<String, Class<? extends B>> subTypes = classes.stream().collect(toIMap(
      Class::getSimpleName,
      c->c.asSubclass(clazz)
    ));
    return new TypeResolverImp(subTypes);
  }

  @Override
  public B parse(final JsonNode json) {
    return clazz.cast(typeRegistry.beanType(typeResolver.resolve(json)).parse(json));
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public JsonNode toJson(final B object) {
    return ((Type)typeRegistry.getType(object)).toJson(object);
  }


  @Override
  boolean isPolymorphic() {
    return true;
  }

  @Override
  boolean isAbstractBean() {
    return true;
  }

  boolean hasDefaultTypeResolver() {
    return rootType.typeResolver.getClass().equals(TypeResolverImp.class);
  }


  private final class TypeResolverImp implements TypeResolver<B> {

    private final IMap<String,Class<? extends B>> subTypes;

    private TypeResolverImp(final IMap<String, Class<? extends B>> subTypes) {
      this.subTypes = subTypes;
    }

    @Override
    public Class<? extends B> resolve(final JsonNode json) {
      return subTypes.get(json.asObject().get("type").asString());
    }

    @Override
    public void addTypeProperty(final BeanBuilder<? extends B> builder) {}

  }



}
