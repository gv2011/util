package com.github.gv2011.util;

import java.lang.ref.SoftReference;
import java.util.function.Supplier;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public final class Constants{

  public static final <T> Constant<T> newCachedConstant(final Constant<? extends T> supplier){
    return new CacheImpl<T>(){
      private SoftReference<T> ref;
      @Override
      protected T getIntern() {
        final SoftReference<T> ref = this.ref;
        return ref==null?null:ref.get();
      }
      @Override
      protected void setIntern(final T value) {
        ref = new SoftReference<>(value);
      }
      @Override
      protected T retrieveValue() {
        return supplier.get();
      }
    };
  }

  public static final <T> Constant<T> newConstant(final Constant<? extends T> supplier){
    return new ConstantImp<T>(){
      @Override
      protected T retrieveValue() {
        return supplier.get();
      }
    };
  }

  public static final <T> LazyConstant<T> newLazyConstant(){
    return new LazyConstantImp<>();
  }

  public static final <T> Constant<T> newLazyCachedConstant(final Supplier<T> supplier){
    return new Constant<T>(){
      private final Object lock = new Object();
      private T value;
      @Override
      public T get() {
        if(value==null){
          synchronized(lock){
            if(value==null){
              value = supplier.get();
            }
          }
        }
        return value;
      }
    };
  }


private Constants(){}

private static abstract class CacheImpl<E> implements Constant<E>{

  protected final Object lock = new Object();

  @Override
  public E get() {
    E result = getIntern();
    if(result==null){
      synchronized(lock){
        result = getIntern();
        if(result==null){
          result = retrieveValue();
          if(result==null) throw new NullPointerException("Retrieved null value.");
          setIntern(result);
        }
      }
    }
    return result;
    }

  protected abstract E retrieveValue();

  protected abstract E getIntern();

  protected abstract void setIntern(E value);
  }

  private static abstract class ConstantImp<T> extends CacheImpl<T>{
    protected T value;
    @Override
    protected T getIntern() {
      return value;
    }
    @Override
    protected void setIntern(final T value) {
      assert this.value==null && value!=null;
      this.value = value;
    }
  }

  private static class LazyConstantImp<T> extends ConstantImp<T> implements LazyConstant<T>{

    @Override
    protected T retrieveValue() {
      if(value==null) throw new IllegalStateException("Value has not been set.");
      return value;
    }

    @Override
    public void set(final T value) {
      synchronized(lock){
        if(this.value!=null){
          //Tolerate if trying to set same value twice.
          if(!value.equals(this.value)) throw new IllegalStateException("Value has already been set.");
        }else{
          this.value = value;
        }
      }

    }


  }

}
