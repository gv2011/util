package com.github.gv2011.util.gcol;

import java.util.Arrays;
import java.util.Collection;

import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.ex.ThrowingFunction;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.icol.Ref;
import com.github.gv2011.util.icol.Single;

public final class ISingle<E> extends Ref<E> implements Single<E>{

  @SuppressWarnings("unchecked")
  public static final <T> Opt<T> ofNullable(final @Nullable T obj){
    return obj==null ? IEmpty.INSTANCE : of(obj);
  }

  public static final <T> ISingle<T> of(final T obj){
    return new ISingle<>(obj);
  }

  private final E element;

  private ISingle(final E element) {
    this.element = element;
  }

  @Override
  public E get() {
    return element;
  }

  @Override
  public <U> ISingle<U> map(final ThrowingFunction<? super E, ? extends U> mapper) {
    return new ISingle<>(mapper.apply(element));
  }

  @SuppressWarnings("unchecked")
  @Override
  public Opt<E> subtract(final Collection<?> other) {
    return other.contains(element) ? this : IEmpty.INSTANCE;
  }

  @Override
  public ISet<E> addElement(final E element) {
    return this.element.equals(element)
      ? this
      : GuavaIcolFactory.INSTANCE.<E>setBuilder().add(this.element).add(element).build()
    ;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T[] toArray(final T[] a) {
    T[] result;
    if(a.length==0){
      result = Arrays.copyOf(a, 1);
      result[0] = (T) element;
    }
    else{
      if(a.length>1) a[1] = null;
      a[0] = (T) element;
      result = a;
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Opt<E> intersection(final Collection<?> other) {
    return other.contains(element) ? this : IEmpty.INSTANCE;
  }

}
