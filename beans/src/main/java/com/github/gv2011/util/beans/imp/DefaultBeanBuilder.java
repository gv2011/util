package com.github.gv2011.util.beans.imp;

import java.lang.reflect.Proxy;

import com.github.gv2011.util.icol.ISortedMap;

final class DefaultBeanBuilder<B> extends BeanBuilderSupport<B>{

  private final BeanTypeSupport<B> beanType;

  DefaultBeanBuilder(final DefaultBeanType<B> beanType) {
    this.beanType = beanType;
  }

  DefaultBeanBuilder(final PolymorphicBeanType<B> beanType) {
    this.beanType = beanType;
  }

  @Override
  protected BeanTypeSupport<B> beanType() {
    return beanType;
  }

  @Override
  protected B create(final ISortedMap<String, Object> imap) {
    return beanType.clazz.cast(Proxy.newProxyInstance(
        beanType.clazz.getClassLoader(),
        new Class<?>[] {beanType.clazz},
        new DefaultInvocationHandler<>(beanType, imap)
    ));
  }

}
