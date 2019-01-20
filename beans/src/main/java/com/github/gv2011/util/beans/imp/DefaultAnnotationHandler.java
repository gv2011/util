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

import static com.github.gv2011.util.CollectionUtils.stream;
import static com.github.gv2011.util.CollectionUtils.toOpt;
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.icol.ICollections.asSet;
import static com.github.gv2011.util.icol.ICollections.emptySet;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.function.Function;

import org.slf4j.Logger;

import com.github.gv2011.util.Nothing;
import com.github.gv2011.util.beans.AbstractRoot;
import com.github.gv2011.util.beans.AnnotationHandler;
import com.github.gv2011.util.beans.Final;
import com.github.gv2011.util.beans.Computed;
import com.github.gv2011.util.beans.DefaultValue;
import com.github.gv2011.util.beans.FixedBooleanValue;
import com.github.gv2011.util.beans.FixedValue;
import com.github.gv2011.util.beans.Parser;
import com.github.gv2011.util.beans.TypeName;
import com.github.gv2011.util.beans.TypeNameStrategy;
import com.github.gv2011.util.beans.TypeResolver;
import com.github.gv2011.util.beans.Validator;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;

final class DefaultAnnotationHandler implements AnnotationHandler{

  private static final Logger LOG = getLogger(DefaultAnnotationHandler.class);

  @Override
  public <B> Opt<Class<? extends TypeResolver<B>>> typeResolver(final Class<? extends B> clazz) {
    return Opt.ofNullable(clazz.getAnnotation(AbstractRoot.class))
      .flatMap(a->{
        @SuppressWarnings({"unchecked"})
        final Class<? extends TypeResolver<B>> typeResolver = (Class<? extends TypeResolver<B>>) a.typeResolver();
        if(typeResolver.equals(TypeResolver.class)) return Opt.empty();
        else return Opt.of(typeResolver);
      })
    ;
  }

  @Override
  public Opt<Class<? extends TypeNameStrategy>> typeNameStrategy(final Class<?> clazz) {
    return Opt.ofNullable(clazz.getAnnotation(AbstractRoot.class))
      .flatMap(a->{
        final Class<? extends TypeNameStrategy> typeNameStrategy = a.typeNameStrategy();
        if(typeNameStrategy.equals(TypeNameStrategy.class)) return Opt.empty();
        else return Opt.of(typeNameStrategy);
      })
    ;
  }

  @Override
  public boolean annotatedAsBean(final Class<?> clazz) {
    final boolean result = clazz.getAnnotation(Final.class)!=null;
    if(result) verify(!declaredAsAbstract(clazz));
    return result;
  }

  @Override
  public boolean declaredAsAbstract(final Class<?> clazz) {
    final boolean result = clazz.getAnnotation(AbstractRoot.class)!=null;
    if(result) verify(!annotatedAsBean(clazz));
    return result;
  }

  @Override
  public boolean isPolymorphicRoot(final Class<?> clazz) {
    final boolean result = Opt.ofNullable(clazz.getAnnotation(AbstractRoot.class))
      .map(a->!subClasses(a).isEmpty())
      .orElse(false)
    ;
    LOG.debug("{} {} a polymorphic root.", clazz, result?"is":"is not");
    return result;
  }

  @Override
  public Opt<String> defaultValue(final Method m) {
    return annotationValue(m, DefaultValue.class, DefaultValue::value);
  }

  @Override
  public Opt<String> fixedValue(final Method m) {
    final Opt<String> fixed = annotationValue(m, FixedValue.class, FixedValue::value);
    final Opt<String> fixedBoolean = annotationValue(m, FixedBooleanValue.class, a->Boolean.toString(a.value()));
    return fixed.merge(fixedBoolean);
  }

  @Override
  public boolean annotatedAsComputed(final Method m) {
    verify(m.getParameterCount()==0);
    return m.isAnnotationPresent(Computed.class);
  }



  @Override
  public ISet<Class<?>> subClasses(final Class<?> clazz) {
    return subClasses(notNull(clazz.getAnnotation(AbstractRoot.class)));
  }

  private ISet<Class<?>> subClasses(final AbstractRoot annotation) {
    final Class<?>[] subClasses = annotation.subClasses();
    verify(subClasses.length>0);
    if(subClasses.length==1 && subClasses[0].equals(Nothing.class)) return emptySet();
    else return asSet(subClasses);
  }

  @Override
  public Opt<String> typeName(final Class<?> clazz) {
    return Opt.ofNullable(clazz.getAnnotation(TypeName.class)).map(TypeName::value);
  }

  private <A extends Annotation, V> Opt<V> annotationValue(
    final Method propertyMethod, final Class<A> annotationClass, final Function<A,V> annotationProperty
  ){
      verify(propertyMethod.getParameterCount()==0);
      final Class<?> clazz = propertyMethod.getDeclaringClass();
      return stream(clazz.getMethods())
        .filter(m->m.getParameterCount()==0)
        .filter(m->m.getName().equals(propertyMethod.getName()))
        .flatOpt(m->Opt.ofNullable(m.getAnnotation(annotationClass)))
        .collect(toOpt())
        .map(annotationProperty)
      ;
  }

  @Override
  public Opt<Class<?>> getImplementingClass(final Class<?> clazz) {
    return
      Opt.ofNullable(clazz.getAnnotation(Final.class))
      .map(Final::implementation)
      .flatMap(impl->impl.equals(Void.class) ? Opt.empty() : Opt.of(impl))
    ;
  }

  @Override
  public Opt<Class<? extends Validator<?>>> getValidatorClass(final Class<?> clazz) {
    return
      Opt.ofNullable(clazz.getAnnotation(Final.class))
      .map(Final::validator)
      .flatMap(impl->impl.equals(Final.NoopValidator.class) ? Opt.empty() : Opt.of(impl))
    ;
  }

  @Override
  public Opt<Class<? extends Parser<?>>> getParser(final Class<?> clazz) {
    return
        Opt.ofNullable(clazz.getAnnotation(Final.class))
        .map(Final::parser)
        .flatMap(impl->impl.equals(Final.NoopParser.class) ? Opt.empty() : Opt.of(impl))
      ;
  }

}
