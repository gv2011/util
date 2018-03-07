package com.github.gv2011.util.beans.imp;

import static com.github.gv2011.util.Verify.verify;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Type;
import java.util.function.Function;

import org.slf4j.Logger;

import com.github.gv2011.util.beans.AnnotationHandler;
import com.github.gv2011.util.beans.BeanBuilder;
import com.github.gv2011.util.json.JsonFactory;


public class DefaultBeanType<T> extends BeanTypeSupport<T>{

  @SuppressWarnings("unused")
  private static final Logger LOG = getLogger(DefaultBeanType.class);

  DefaultBeanType(
    final Class<T> beanClass,
    final JsonFactory jf,
    final AnnotationHandler annotationHandler,
    final Function<Type,AbstractType<?>> registry
  ) {
    super(beanClass, jf, annotationHandler, registry);
    verify(beanClass.isInterface(), beanClass::toString);
  }

  @Override
  public BeanBuilder<T> createBuilder() {
    return new DefaultBeanBuilder<>(this);
  }

}
