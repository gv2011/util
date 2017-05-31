package com.github.gv2011.util.tstr;

import com.github.gv2011.util.Equal;

public abstract class TypedString<T extends TypedString<T>> implements Comparable<TypedString<?>>{

  protected abstract T self();

  protected abstract Class<T> clazz();

  @Override
  public abstract String toString();

  @Override
  public int hashCode(){
    return clazz().hashCode() * 31 + toString().hashCode();
  }

  @Override
  public final boolean equals(final Object obj) {
    return Equal.equal(this, obj, TypedString.class, o->{
      return clazz().equals(o.clazz()) && toString().equals(o.toString());
    });
  }

  @Override
  public final int compareTo(final TypedString<?> o) {
    int result = clazz().getName().compareTo(o.clazz().getName());
    if(result==0){
      result = toString().compareTo(o.toString());
    }
    return result;
  }


}
