package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.CollectionUtils.toSingle;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.lang.reflect.Method;
import java.util.Optional;

import com.github.gv2011.util.ReflectionUtils;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.beans.Type;
import com.github.gv2011.util.beans.TypeResolver;

class PolymorphicAbstractBeanRootType<B> extends AbstractPolymorphicBeanSupport<B> {

  static final String TYPE_PROPERTY = "type";

  private final TypeResolver<B> typeResolver;

  PolymorphicAbstractBeanRootType(final DefaultTypeRegistry typeRegistry, final Class<B> clazz) {
    super(typeRegistry, clazz);
    typeResolver = getAnnotatedTypeResolver()
      .orElseGet(()->createDefaultTypeResolver(typeRegistry.annotationHandler.subClasses(clazz)))
    ;
  }

  @Override
  PolymorphicAbstractBeanRootType<B> rootType() {
    return this;
  }

  @Override
  TypeResolver<B> typeResolver() {
    return typeResolver;
  }

  private void checkTypeMethod(final Class<B> clazz, final DefaultTypeRegistry typeRegistry) {
    final Method typeMethod = call(() -> clazz.getMethod(TYPE_PROPERTY));
    final Type<?> typeType = typeRegistry.type(typeMethod.getReturnType());
    verify(typeType instanceof AbstractElementaryType);
  }

//  @SuppressWarnings("unchecked")
//  private Optional<PolymorphicAbstractBeanRootType<? super B>> rootType(
//      final DefaultTypeRegistry typeRegistry, final Class<B> clazz) {
//    final Class<?> rootClass =
//        ReflectionUtils.getAllInterfaces(clazz).stream().concat(XStream.of(clazz))
//            .filter(typeRegistry.annotationHandler::isPolymorphicRoot)
//            .collect(toSingle());
//    if (rootClass.equals(clazz)) return Optional.empty();
//    else return Optional.of((PolymorphicAbstractBeanRootType<? super B>) typeRegistry.abstractBeanType(rootClass));
//  }



  @SuppressWarnings("unchecked")
  final <U> PolymorphicAbstractBeanRootType<? super U> asSuper(final Class<U> clazz) {
    verify(this.clazz.isAssignableFrom(clazz));
    return (PolymorphicAbstractBeanRootType<? super U>) this;
  }


}
