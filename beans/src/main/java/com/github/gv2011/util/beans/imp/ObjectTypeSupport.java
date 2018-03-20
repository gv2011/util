package com.github.gv2011.util.beans.imp;

public abstract class ObjectTypeSupport<B> extends TypeSupport<B>{

  protected ObjectTypeSupport(final Class<B> clazz) {
    super(clazz);
  }

  public abstract boolean isAbstract();

}
