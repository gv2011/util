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

import static com.github.gv2011.util.Verify.verify;

import java.util.Optional;

import com.github.gv2011.util.beans.TypeNameStrategy;
import com.github.gv2011.util.beans.TypeResolver;

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

  final Optional<String> typePropertyName() {
    final boolean hasDefaultTypeResolver = typeResolver().getClass().equals(DefaultTypeResolver.class);
    return hasDefaultTypeResolver ? Optional.of(DefaultTypeResolver.TYPE_PROPERTY) : Optional.empty();
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
