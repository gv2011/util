package com.github.gv2011.util.beans;

import static com.github.gv2011.util.beans.Constructor.Variant.PARAMETER_NAMES;

import java.time.Instant;

public final class AbstractBeanTestBean2Imp extends AbstractBean<AbstractBeanTestBean2> implements AbstractBeanTestBean2{

  private static final ClassCache CLASS_CACHE = AbstractBean.createClassCache(AbstractBeanTestBean2.class);

  private final String street;
  private final Integer number;
  private final Instant date;

  @Constructor(PARAMETER_NAMES)
  public AbstractBeanTestBean2Imp(final String street, final Instant date) {
    this.street = street;
    number = 0;
    this.date = date;
  }

  @Override
  protected Class<AbstractBeanTestBean2> clazz() {
    return AbstractBeanTestBean2.class;
  }

  @Override
  protected AbstractBeanTestBean2Imp self() {
    return this;
  }

  @Override
  protected ClassCache classCache() {
    return CLASS_CACHE;
  }

  @Override
  public String street() {
    return street;
  }

  @Override
  public Integer number() {
    return number;
  }

  @Override
  public Instant date() {
    return date;
  }

}
