package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;

import com.github.gv2011.util.ReflectionUtils;
import com.github.gv2011.util.XStream;
import com.github.gv2011.util.beans.AnnotationHandler;
import com.github.gv2011.util.beans.TypeNameStrategy;
import com.github.gv2011.util.json.JsonFactory;

final class BeanFactory {

  private static final Logger LOG = getLogger(BeanFactory.class);

  private JsonFactory jf;
  private AnnotationHandler annotationHandler;
  private Function<Type,AbstractType<?>> registry;

  private String isAbstractOrBeanClassCandidate(final Class<?> clazz) {
    final String whyNot;
    if(!clazz.isInterface())
      whyNot = "not an interface";
    else if(clazz.getTypeParameters().length!=0)
      whyNot = "parameterized class";
    else if(Arrays.stream(clazz.getInterfaces()).anyMatch(this::isBeanClass))
      whyNot = "is subclass of a bean class";
    else {
      whyNot = "";
    }
    return whyNot;
  }


  final boolean isBeanClass(final Class<?> clazz) {
    final String notBeanReason;
    final String precondition = isAbstractOrBeanClassCandidate(clazz);
    if(!precondition.isEmpty())
      notBeanReason = precondition;
    else if(annotationHandler.declaredAsAbstract(clazz))
      notBeanReason = "annotated as abstract";
    else if(!Arrays.stream(clazz.getMethods()).filter(this::isPropertyMethod).findAny().isPresent())
      notBeanReason = "has no properties";
    else {
      notBeanReason = "";
    }
    if(notBeanReason.isEmpty()) {
      LOG.trace("{} is a bean.", clazz);
      return true;
    }
    else {
      LOG.trace("{} is not a bean ({}).", clazz, notBeanReason);
      verify(!annotationHandler.annotatedAsBean(clazz), ()->notBeanReason);
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

  boolean isAbstractPolymorphicRootBeanClass(final Class<?> clazz) {
    final String notBeanReason;
    final String precondition = isAbstractBeanCandidate(clazz);
    if(!precondition.isEmpty())
      notBeanReason = precondition;
    else if(!annotationHandler.isPolymorphicRoot(clazz))
      notBeanReason = "not annotated as root";
    else {
      notBeanReason = "";
    }
    if(notBeanReason.isEmpty()) {
      LOG.trace("{} is a polymorphic root.", clazz);
      return true;
    }
    else {
      LOG.trace("{} is not a polymorphic root ({}).", clazz, notBeanReason);
      verify(!annotationHandler.annotatedAsBean(clazz), ()->notBeanReason);
      return false;
    }
  }

  boolean isAbstractPolymorphicIntermediateBeanClass(final Class<?> clazz) {
    final String notBeanReason;
    final String precondition = isAbstractBeanCandidate(clazz);
    if(!precondition.isEmpty())
      notBeanReason = precondition;
    else if(annotationHandler.isPolymorphicRoot(clazz))
      notBeanReason = "annotated as root";
    else {
      notBeanReason = "";
    }
    if(notBeanReason.isEmpty()) {
      LOG.trace("{} is a polymorphic intermediate.", clazz);
      return true;
    }
    else {
      LOG.trace("{} is not a polymorphic intermediate ({}).", clazz, notBeanReason);
      verify(!annotationHandler.annotatedAsBean(clazz), ()->notBeanReason);
      return false;
    }
  }

  public boolean isSupported(final Class<?> clazz) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }


  private boolean isPropertyMethod(final Method m) {
    assert m.getDeclaringClass().isInterface();
    return m.getParameterCount()==0;
  }


  <T> AbstractType<T> createBeanType(final Class<T> clazz) {
    assert isBeanClass(clazz);
    final Optional<Class<?>> superBean = ReflectionUtils.getAllInterfaces(clazz).parallelStream()
      .toOptional(annotationHandler::isPolymorphicRoot)
    ;
    if(!superBean.isPresent()) return new DefaultBeanType<>(clazz, jf, annotationHandler, registry);
    else{
      final PolymorphicAbstractBeanRootType<? super T> rootType = getRootType(clazz);
      return new PolymorphicConcreteBeanType<>(
        clazz, jf, annotationHandler, registry, //same as DefaultBeanType
        rootType.typePropertyName(), getTypeNameStrategy()
      );
    }
  }

  private <B> PolymorphicAbstractBeanRootType<? super B> getRootType(final Class<B> clazz) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  private TypeNameStrategy getTypeNameStrategy() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }
//  private PolymorphicAbstractBeanRootType<R> findRootType(final Class<B> clazz) {
//    final PolymorphicAbstractBeanRootType<?> result = (PolymorphicAbstractBeanRootType<?>) registry.type(
//      getAllInterfaces(clazz).stream()
//      .filter(registry.annotationHandler::isPolymorphicRoot)
//      .collect(toSingle())
//    );
//    return result.asSuper(clazz);
//  }

  final Optional<Class<?>> tryGetBeanInterface(final Class<?> clazz){
    if(clazz.getTypeParameters().length!=0) return Optional.empty();
    else {
      final Optional<Class<?>> fromSuper = Optional.ofNullable(clazz.getSuperclass())
        .flatMap(this::tryGetBeanInterface)
      ;
      final Optional<Class<?>> fromSuperInterfaces = XStream.of(clazz.getInterfaces())
        .flatOptional(this::tryGetBeanInterface)
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

  final boolean isAbstractPolymorphicBeanClass(final Class<?> clazz) {
    return
      isAbstractPolymorphicRootBeanClass(clazz) ||
      isAbstractPolymorphicIntermediateBeanClass(clazz)
    ;
  }


}
