package com.github.gv2011.util.beans.cglib;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.function.Function;

import com.github.gv2011.util.beans.AnnotationHandler;
import com.github.gv2011.util.beans.imp.BeanFactory;
import com.github.gv2011.util.beans.imp.DefaultBeanFactory;
import com.github.gv2011.util.beans.imp.DefaultTypeRegistry;
import com.github.gv2011.util.beans.imp.ObjectTypeSupport;
import com.github.gv2011.util.beans.imp.TypeSupport;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonFactory;

final class CglibBeanFactory extends BeanFactory {

  private final BeanFactory interfaceBeanFactory;

  CglibBeanFactory(
    final JsonFactory jf, final AnnotationHandler annotationHandler, final DefaultTypeRegistry registry
  ) {
    super(jf, annotationHandler, registry);
    interfaceBeanFactory = new DefaultBeanFactory(jf, annotationHandler, registry);
  }

  @Override
  protected String isProxyable(final Class<?> clazz) {
    return clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) ? "" : "not abstract";
  }

  @Override
  protected boolean isPropertyMethod2(final Method m) {
    return Modifier.isAbstract(m.getModifiers());
  }

  @Override
  public <B> Opt<ObjectTypeSupport<B>> tryCreate(final Class<B> clazz) {
    if(clazz.isInterface()) return interfaceBeanFactory.tryCreate(clazz);
    else return super.tryCreate(clazz);
  }

@Override
  protected <B> ObjectTypeSupport<B> createRegularBeanType(
    final Class<B> clazz,
    final JsonFactory jf,
    final AnnotationHandler annotationHandler,
    final Function<Type,TypeSupport<?>> registry
  ) {
    return new CglibBeanType<>(clazz, jf, annotationHandler, this);
  }

}
