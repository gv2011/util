package com.github.gv2011.util.beans.cglib;

import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Type;
import java.util.function.Function;

import org.slf4j.Logger;

import com.github.gv2011.util.beans.AnnotationHandler;
import com.github.gv2011.util.beans.BeanBuilder;
import com.github.gv2011.util.beans.imp.AbstractType;
import com.github.gv2011.util.beans.imp.BeanTypeSupport;
import com.github.gv2011.util.json.JsonFactory;


final class CglibBeanType<T> extends BeanTypeSupport<T>{

  @SuppressWarnings("unused")
  private static final Logger LOG = getLogger(CglibBeanType.class);

  CglibBeanType(
    final Class<T> beanClass,
    final JsonFactory jf,
    final AnnotationHandler annotationHandler,
    final Function<Type,AbstractType<?>> registry
  ) {
    super(beanClass, jf, annotationHandler, registry);
  }

  @Override
  public final BeanBuilder<T> createBuilder() {
      return new CglibBeanBuilder<>(this);
  }

}
