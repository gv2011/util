package com.github.gv2011.util.beans.imp;

public abstract class ObjectTypeSupport<B> extends TypeSupport<B>{

  protected ObjectTypeSupport(final Class<B> clazz) {
    super(clazz);
  }

  public abstract boolean isAbstract();

  @Override
  public final boolean hasStringForm() {
    return false;
  }

  @Override
  public final boolean isForeignType() {
    return false;
  }

  @Override
  public B parse(final String string) {
    return parse(jf().deserialize(string).asObject());
  }

}
