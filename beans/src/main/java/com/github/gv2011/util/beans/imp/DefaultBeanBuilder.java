package com.github.gv2011.util.beans.imp;

import java.lang.reflect.Proxy;
import java.util.function.UnaryOperator;

import com.github.gv2011.util.icol.ISortedMap;

final class DefaultBeanBuilder<B> extends BeanBuilderSupport<B>{

  private final BeanTypeSupport<B> beanType;

  DefaultBeanBuilder(
    final DefaultBeanType<B> beanType,
    final UnaryOperator<B> resultWrapper,
    final UnaryOperator<B> validator
  ) {
    super(resultWrapper, validator);
    this.beanType = beanType;
  }

  DefaultBeanBuilder(
    final PolymorphicBeanType<B> beanType,
    final UnaryOperator<B> resultWrapper,
    final UnaryOperator<B> validator
  ) {
    super(resultWrapper, validator);
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
