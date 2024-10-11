package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.Verify.verify;

import com.github.gv2011.util.beans.TypeNameStrategy;
import com.github.gv2011.util.beans.TypeResolver;
import com.github.gv2011.util.icol.Opt;

final class PolymorphicRootType<B> extends AbstractPolymorphicSupport<B> {

  private final TypeResolver<B> typeResolver;
  private final TypeNameStrategy typeNameStrategy;

  PolymorphicRootType(
    final DefaultTypeRegistry registry, final Class<B> clazz,
    final TypeResolver<B> typeResolver, final TypeNameStrategy typeNameStrategy
  ) {
    super(registry, clazz);
    this.typeResolver = typeResolver;
    this.typeNameStrategy = typeNameStrategy;
  }

  @Override
  PolymorphicRootType<B> rootType() {
    return this;
  }

  @Override
  TypeResolver<B> typeResolver() {
    return typeResolver;
  }

  TypeNameStrategy typeNameStrategy() {
    return typeNameStrategy;
  }

  final Opt<String> typePropertyName() {
    return typeResolver.typePropertyName();
  }


  @SuppressWarnings("unchecked")
  final <U> PolymorphicRootType<? super U> asSuper(final Class<U> clazz) {
    verify(this.clazz.isAssignableFrom(clazz));
    return (PolymorphicRootType<? super U>) this;
  }

  @Override
  <T2> PolymorphicRootType<T2> castTo(final Class<T2> clazz) {
    return (PolymorphicRootType<T2>) super.castTo(clazz);
  }

}
