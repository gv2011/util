package com.github.gv2011.util;

import java.util.NoSuchElementException;

final class EitherImp<A,B> implements Either<A,B>{

  public static <A,B> Either<A,B> newThis(final A a) {
    return new EitherImp<>(true, a);
  }

  public static <A,B> Either<A,B> newThat(final B b) {
    return new EitherImp<>(true, b);
  }

  private final boolean isThis;
  private final Object value;

  private EitherImp(final boolean isThis, final Object value) {
    this.isThis = isThis;
    this.value = value;
  }

  @Override
  public boolean isThis() {
    return isThis;
  }

  @Override
  @SuppressWarnings("unchecked")
  public A getThis() {
    if(!isThis) throw new NoSuchElementException();
    return (A) value;
  }

  @Override
  @SuppressWarnings("unchecked")
  public B getThat() {
    if(isThis) throw new NoSuchElementException();
    return (B) value;
  }

  @Override
  public Object get() {
    return value;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if(this==obj) return true;
    else if(!(obj instanceof Either)) return false;
    else return value.equals(((Either<?,?>)obj).get());
  }

  @Override
  public String toString() {
    return value.toString();
  }



}
