package com.github.gv2011.util.beans.imp;

import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Type;
import java.util.function.Function;

import org.slf4j.Logger;

import com.github.gv2011.util.beans.AnnotationHandler;
import com.github.gv2011.util.beans.BeanHandlerFactory;
import com.github.gv2011.util.json.JsonFactory;

public final class DefaultBeanFactory extends BeanFactory{

  static final class DefaultBeanFactoryBuilder implements BeanFactoryBuilder{
    @Override
    public BeanFactory build(
      final JsonFactory jf, final AnnotationHandler annotationHandler, final DefaultTypeRegistry registry, final BeanHandlerFactory beanHandlerFactory
    ) {
      return new DefaultBeanFactory(jf, annotationHandler, registry, beanHandlerFactory);
    }
  }

  @SuppressWarnings("unused")
  private static final Logger LOG = getLogger(DefaultBeanFactory.class);

  public DefaultBeanFactory(
    final JsonFactory jf,
    final AnnotationHandler annotationHandler,
    final DefaultTypeRegistry registry,
    final BeanHandlerFactory beanHandlerFactory
  ) {
    super(jf,annotationHandler,registry, beanHandlerFactory);
  }

  @Override
  protected String isProxyable(final Class<?> clazz) {
    return clazz.isInterface() ? "" : "not an interface";
  }

  @Override
  protected <B> ObjectTypeSupport<B> createRegularBeanType(
    final Class<B> clazz,
    final JsonFactory jf,
    final AnnotationHandler annotationHandler,
    final Function<Type,TypeSupport<?>> registry
  ) {
    return new DefaultBeanType<>(clazz, jf, annotationHandler, this, beanHandlerFactory.createBeanHandler(clazz));
  }

}
