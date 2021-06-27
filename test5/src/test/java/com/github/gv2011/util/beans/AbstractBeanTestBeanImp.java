package com.github.gv2011.util.beans;

public final class AbstractBeanTestBeanImp extends AbstractBean<AbstractBeanTestBean> implements AbstractBeanTestBean{

  private static final ClassCache CLASS_CACHE = AbstractBean.createClassCache(AbstractBeanTestBean.class);

  private final String street;
  private final Integer number;

  public AbstractBeanTestBeanImp(final String street, final Integer number) {
    this.street = street;
    this.number = number;
  }

  @Override
  protected Class<AbstractBeanTestBean> clazz() {
    return AbstractBeanTestBean.class;
  }

  @Override
  protected AbstractBeanTestBeanImp self() {
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
  public String toString() {
    return street+" "+number;
  }

  public String superToString() {
    return super.toString();
  }

}
