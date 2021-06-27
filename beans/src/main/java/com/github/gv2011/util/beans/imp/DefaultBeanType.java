package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.Verify.verify;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.github.gv2011.util.beans.AnnotationHandler;
import com.github.gv2011.util.beans.ExtendedBeanBuilder;
import com.github.gv2011.util.json.JsonFactory;


public class DefaultBeanType<T> extends BeanTypeSupport<T>{

  @SuppressWarnings("unused")
  private static final Logger LOG = getLogger(DefaultBeanType.class);

  DefaultBeanType(
    final Class<T> beanClass,
    final JsonFactory jf,
    final AnnotationHandler annotationHandler,
    final BeanFactory beanFactory
  ) {
    super(beanClass, jf, annotationHandler, beanFactory);
    verify(beanClass.isInterface(), beanClass::toString);
  }

  @Override
  public ExtendedBeanBuilder<T> createBuilder() {
    return constructor()
      .map(c->(ExtendedBeanBuilder<T>) new ImplementationBeanBuilder<>(this, resultWrapper, validator, c))
      .orElseGet(()->new DefaultBeanBuilder<>(this, resultWrapper, validator))
    ;
  }

  @Override
  public final boolean isAbstract() {
    return false;
  }

}
