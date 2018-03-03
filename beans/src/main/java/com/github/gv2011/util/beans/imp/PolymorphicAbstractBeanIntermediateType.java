package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.Verify.verify;

import java.util.Optional;

import com.github.gv2011.util.beans.TypeResolver;

final class PolymorphicAbstractBeanIntermediateType<B extends R, R> extends AbstractPolymorphicBeanSupport<B>{

  private final Optional<TypeResolver<B>> typeResolver;
  private final PolymorphicAbstractBeanRootType<R> rootType;


  PolymorphicAbstractBeanIntermediateType(
    final DefaultTypeRegistry registry,
    final PolymorphicAbstractBeanRootType<R> rootType, final Class<B> clazz
  ) {
    super(registry, clazz);
    this.rootType = rootType;
    typeResolver = getAnnotatedTypeResolver();
    verify(!typeResolver.isPresent(), ()->"Not yet supported.");
  }


  @Override
  PolymorphicAbstractBeanRootType<? super B> rootType() {
    return rootType;
  }

  @Override
  TypeResolver<? super B> typeResolver() {
    return typeResolver.isPresent() ? typeResolver.get() : rootType().typeResolver();
  }

}
