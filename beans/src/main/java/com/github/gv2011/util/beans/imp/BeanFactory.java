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
import static com.github.gv2011.util.CollectionUtils.atMostOne;
import static com.github.gv2011.util.CollectionUtils.toOpt;
import static com.github.gv2011.util.ReflectionUtils.getAllInterfaces;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.toIMap;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Function;

import org.slf4j.Logger;

import com.github.gv2011.util.ReflectionUtils;
import com.github.gv2011.util.ann.VisibleForTesting;
import com.github.gv2011.util.beans.AnnotationHandler;
import com.github.gv2011.util.beans.Elementary;
import com.github.gv2011.util.beans.TypeNameStrategy;
import com.github.gv2011.util.beans.TypeResolver;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;

public abstract class BeanFactory{

  private static final Logger LOG = getLogger(DefaultBeanFactory.class);

  private final JsonFactory jf;
  private final AnnotationHandler annotationHandler;
  private final DefaultTypeRegistry registry;


  protected BeanFactory(
    final JsonFactory jf,
    final AnnotationHandler annotationHandler,
    final DefaultTypeRegistry registry
  ) {
    this.jf = jf;
    this.annotationHandler = annotationHandler;
    this.registry = registry;
  }


  private String isAbstractOrBeanClassCandidate(final Class<?> clazz) {
    String whyNot = isProxyable(clazz);
    if(whyNot.isEmpty()){
      if(clazz.getTypeParameters().length!=0)
        whyNot = "parameterized class";
      else{
        final Opt<Class<?>> beanSuper = getAllInterfaces(clazz).parallelStream().tryFindAny(this::isBeanClass);
        if(beanSuper.isPresent()) whyNot = format("{} is subclass of bean class {}.", clazz, beanSuper.get());
      }
    }
    return whyNot;
  }

  protected abstract String isProxyable(final Class<?> clazz);

  protected final AnnotationHandler annotationHandler(){
    return annotationHandler;
  }


  @VisibleForTesting
  final boolean isBeanClass(final Class<?> clazz) {
    return notBeanReason(clazz).isEmpty();
  }

  @VisibleForTesting
  final String notBeanReason(final Class<?> clazz) {
    final String notBeanReason;
    final String elementary = checkElementary(clazz);
    if(!elementary.isEmpty()) notBeanReason = elementary;
    else {
      final String precondition = isAbstractOrBeanClassCandidate(clazz);
      if(!precondition.isEmpty())
        notBeanReason = precondition;
      else if(annotationHandler.declaredAsAbstract(clazz))
        notBeanReason = "annotated as abstract";
      else if(!Arrays.stream(clazz.getMethods()).filter(m->isPropertyMethod(clazz, m)).findAny().isPresent())
        notBeanReason = "has no properties";
      else {
        notBeanReason = "";
      }
    }
    if(notBeanReason.isEmpty()) {
      LOG.trace("{} is a bean.", clazz);
    }
    else {
      LOG.trace("{} is not a bean ({}).", clazz, notBeanReason);
      verify(!annotationHandler.annotatedAsBean(clazz), ()->notBeanReason);
    }
    return notBeanReason;
  }

  private String checkElementary(Class<?> clazz) {
    if(Elementary.class.isAssignableFrom(clazz)) return "is subclass of "+Elementary.class.getSimpleName();
    else return "";
  }


  private final boolean isRegularBeanClass(final Class<?> clazz){
    final String notRegularReason;
    final String precondition = notBeanReason(clazz);
    if(!precondition.isEmpty())
      notRegularReason = precondition;
    else if(tryGetRoot(clazz).isPresent())
      notRegularReason = "is polymorphic";
    else {
      notRegularReason = "";
    }
    if(notRegularReason.isEmpty()) {
      LOG.trace("{} is a regular bean.", clazz);
      return true;
    }
    else {
      LOG.trace("{} is a polymorphic bean ({}).", clazz, notRegularReason);
      return false;
    }
  }

  private String isAbstractBeanCandidate(final Class<?> clazz) {
    final String whyNot;
    final String precondition = isAbstractOrBeanClassCandidate(clazz);
    if(!precondition.isEmpty())
      whyNot = precondition;
    else if(!annotationHandler.declaredAsAbstract(clazz))
      whyNot = "not annotated as abstract";
    else {
      whyNot = "";
    }
    return whyNot;
  }

  private boolean isPolymorphicRootClass(final Class<?> clazz) {
    return notPolymorphicRootClassReason(clazz).isEmpty();
  }

  @VisibleForTesting
  String notPolymorphicRootClassReason(final Class<?> clazz) {
    final String reason;
    final String precondition = isAbstractBeanCandidate(clazz);
    if(!precondition.isEmpty())
      reason = precondition;
    else if(!annotationHandler.isPolymorphicRoot(clazz))
      reason = "not annotated as root";
    else {
      verify(tryGetRoot(clazz), o->!o.isPresent(), o->format("{} is below other root: {}.", clazz, o.get()));
      reason = "";
    }
    if(reason.isEmpty()) {
      LOG.trace("{} is a polymorphic root.", clazz);
    }
    else {
      LOG.trace("{} is not a polymorphic root ({}).", clazz, reason);
    }
    return reason;
  }

  private boolean isPolymorphicIntermediateClass(final Class<?> clazz) {
    return notPolymorphicIntermediateClassReason(clazz).isEmpty();
  }

  private String notPolymorphicIntermediateClassReason(final Class<?> clazz) {
    final String reason;
    final String precondition = isAbstractBeanCandidate(clazz);
    if(!precondition.isEmpty())
      reason = precondition;
    else if(annotationHandler.isPolymorphicRoot(clazz))
      reason = "annotated as root";
    else if(!tryGetRoot(clazz).isPresent())
      reason = "has no root";
    else {
      reason = "";
    }
    if(reason.isEmpty()) {
      LOG.trace("{} is a polymorphic intermediate.", clazz);
    }
    else {
      LOG.trace("{} is not a polymorphic intermediate ({}).", clazz, reason);
    }
    return reason;
  }

  @SuppressWarnings("unchecked")
  private <B> Opt<Class<? super B>> tryGetRoot(final Class<B> clazz) {
    final ISet<Class<?>> roots = getAllInterfaces(clazz).parallelStream()
      .filter(this::isPolymorphicRootClass)
      .collect(toISet())
    ;
    return atMostOne(roots, ()->format("Multiple roots: {}", roots))
      .map(c->{
        assert isPolymorphicRootClass(c) : c;
        verify(c.isAssignableFrom(clazz) && !c.equals(clazz));
        return (Class<? super B>) c;
      })
    ;
  }


  public boolean isSupported(final Class<?> clazz) {
    return
      isBeanClass(clazz) ||
      isAbstractPolymorphicClass(clazz)
    ;
  }

  public String notSupportedReason(final Class<?> clazz) {
    String result;
    final String notCandidate = isAbstractOrBeanClassCandidate(clazz);
    if(!notCandidate.isEmpty()) result = notCandidate;
    else{
      final String notBeanReason = notBeanReason(clazz);
      if(notBeanReason.isEmpty()){
        result = "";
      }
      else{
        final String notAbstract = isAbstractBeanCandidate(clazz);
        if(!notAbstract.isEmpty()){
          result = notBeanReason + " and " + notAbstract;
        }
        else{
          final String notRoot = notPolymorphicRootClassReason(clazz);
          if(notRoot.isEmpty()) result = "";
          else{
            final String notIntermediate = notPolymorphicIntermediateClassReason(clazz);
            if(notIntermediate.isEmpty()) result = "";
            else{
              result = notRoot + " and " + notIntermediate;
            }
          }
        }
      }
    }
    assert result.isEmpty() == isSupported(clazz) : result+" "+clazz;
    return result;
  }


  public final boolean isPropertyMethod(Class<?> owner, final Method m) {
    boolean result;
    if(m.getParameterCount()!=0) result = false;
    else if(isObjectMethod(m)) result = false;
    else {
      result = isPropertyMethod2(m);
      if(result){
        final Class<?> returnType = m.getReturnType();
        verify(
          returnType!=void.class && returnType!=Void.class, 
          ()->format("Method {}:{} has void return type.", owner.getName(), m.getName())
        );
      }
    }
    return result;
  }

  protected boolean isPropertyMethod2(final Method m){
    return true;
  }


  protected final boolean isObjectMethod(final Method m){
    verify(m.getParameterCount()==0);
    return ReflectionUtils.OBJECT_PROPERTY_METHOD_NAMES.contains(m.getName());
  }

  private final boolean isAbstractPolymorphicClass(final Class<?> clazz) {
    return
      isPolymorphicRootClass(clazz) ||
      isPolymorphicIntermediateClass(clazz)
    ;
  }

  public <B> Opt<ObjectTypeSupport<B>> tryCreate(final Class<B> clazz) {
    if(isBeanClass(clazz)){
      if(isRegularBeanClass(clazz)) return Opt.of(createRegularBeanType(clazz));
      else return Opt.of(createPolymorphicBean(clazz));
    }
    else if(isPolymorphicRootClass(clazz)) return Opt.of(createPolymorphicRoot(clazz));
    else if(isPolymorphicIntermediateClass(clazz)) return Opt.of(createPolymorphicIntermediate(clazz));
    else{
      assert !isSupported(clazz);
      return Opt.empty();
    }
  }

  private <B> ObjectTypeSupport<B> createRegularBeanType(final Class<B> clazz) {
    return createRegularBeanType(clazz, jf, annotationHandler, registry::type);
  }


  protected abstract <B> ObjectTypeSupport<B> createRegularBeanType(
    Class<B> clazz, JsonFactory jf, AnnotationHandler annotationHandler, Function<Type,TypeSupport<?>> registry
  );


  protected <B> ObjectTypeSupport<B> createPolymorphicBean(final Class<B> clazz) {
    final PolymorphicRootType<? super B> rootType = rootTypeForRootClass(tryGetRoot(clazz).get());
    return new PolymorphicBeanType<>(
      clazz, jf, annotationHandler, this, //same as DefaultBeanType
      rootType.typePropertyName(), rootType.typeNameStrategy()
    );
  }


  private <B> PolymorphicRootType<B> rootTypeForRootClass(final Class<B> rootClass) {
    verify(isPolymorphicRootClass(rootClass));
    final PolymorphicRootType<?> type = (PolymorphicRootType<?>) registry.type(rootClass);
    return type.castTo(rootClass);
  }


  private <B> ObjectTypeSupport<B> createPolymorphicRoot(final Class<B> clazz) {
    final TypeNameStrategy typeNameStrategy = annotationHandler.typeNameStrategy(clazz)
      .map(s->(TypeNameStrategy)call(()->s.getDeclaredConstructor().newInstance()))
      .orElse(Class::getSimpleName)
    ;
    final TypeResolver<B> typeResolver = getAnnotatedTypeResolver(clazz)
      .orElseGet(()->createDefaultTypeResolver(clazz, typeNameStrategy))
    ;
    return new PolymorphicRootType<>(registry, clazz, typeResolver, typeNameStrategy);
  }

  private <B> Opt<TypeResolver<B>> getAnnotatedTypeResolver(final Class<B> clazz) {
    return annotationHandler.typeResolver(clazz)
      .map(c->new TypeResolverWrapper<>(clazz, call(()->c.getDeclaredConstructor().newInstance())))
    ;
  }

  private <B> TypeResolver<B> createDefaultTypeResolver(
    final Class<B> clazz, final TypeNameStrategy typeNameStrategy
  ) {
    final IMap<String, Class<? extends B>> subTypes = annotationHandler.subClasses(clazz).stream()
      .collect(toIMap(
        typeNameStrategy::typeName,
        c -> c.asSubclass(clazz)
      ))
    ;
    return new DefaultTypeResolver<>(subTypes);
  }


  private <B extends R,R> ObjectTypeSupport<B> createPolymorphicIntermediate(final Class<B> clazz) {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    final PolymorphicRootType<R> rootType = (PolymorphicRootType)rootTypeForRootClass(tryGetRoot(clazz).get());
    return new PolymorphicIntermediateType<>(registry, rootType, clazz);
  }


  public Opt<Class<?>> tryGetBeanInterface(final Class<?> clazz) {
    return getAllInterfaces(clazz).parallelStream()
      .filter(this::isBeanClass)
      .collect(toOpt())
    ;
  }

  /**
   * Verifies results of external TypeResolver.
   */
  private static final class TypeResolverWrapper<B> implements TypeResolver<B>{

    private final Class<B> clazz;
    private final TypeResolver<?> delegate;

    private TypeResolverWrapper(final Class<B> clazz, final TypeResolver<?> delegate) {
      this.clazz = clazz;
      this.delegate = delegate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends B> resolve(final JsonNode json) {
      final Class<?> result = delegate.resolve(json);
      verify(clazz.isAssignableFrom(result) && !clazz.equals(result) && result.isInterface());
      return (Class<? extends B>) result;
    }
  }

  public Function<Type, TypeSupport<?>> registry() {
    return registry::type;
  }

}
