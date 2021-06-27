package com.github.gv2011.util.beans.imp;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.github.gv2011.util.icol.ISortedMap;

final class ImplementationBeanBuilder<B> extends BeanBuilderSupport<B>{

  private final BeanTypeSupport<B> beanType;
  private final Function<ISortedMap<String, Object>, B> constructor;

  ImplementationBeanBuilder(
    final DefaultBeanType<B> beanType,
    final UnaryOperator<B> resultWrapper,
    final UnaryOperator<B> validator,
    final Function<ISortedMap<String, Object>, B> constructor
  ) {
    super(resultWrapper, validator);
    this.beanType = beanType;
    this.constructor = constructor;
  }

  @Override
  protected BeanTypeSupport<B> beanType() {
    return beanType;
  }

  @Override
  protected B create(final ISortedMap<String, Object> imap) {
    return constructor.apply(imap);
  }

}
