package com.github.gv2011.util;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */




import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.run;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.lang.ref.SoftReference;

import com.github.gv2011.util.ex.ThrowingConsumer;
import com.github.gv2011.util.ex.ThrowingSupplier;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public final class Constants{

  private Constants(){staticClass();}

  /**
   * The value of the constant is lazily retrieved from the supplier. It is cached in a soft reference, so the
   * supplier may be called multiple times. The supplier must guarantee to return always the same value.
   */
  public static final <T> CachedConstant<T> softRefConstant(final Constant<? extends T> supplier){
    return new SoftRefConstant<>(supplier);
  }

  /**
   * The value of the constant must be set before it is retrieved.
   */
  public static final <T> CachedConstant<T> cachedConstant(){
    return new CloseableConstantImp<>(()->{throw new IllegalStateException("Value has not been set.");}, t->{});
  }

  /**
   * The value of the constant is lazily retrieved from the supplier. It is cached forever, so the
   * supplier is called at most once.
   */
  public static final <T> CachedConstant<T> cachedConstant(final ThrowingSupplier<? extends T> supplier){
    return new CloseableConstantImp<>(supplier, t->{});
  }

  /**
   * The value of the constant is lazily retrieved from the supplier. It is cached forever, so the
   * supplier is called at most . The supplier must guarantee to return always the same value.
   */
  public static final <T extends AutoCloseable> CloseableCachedConstant<T> closeableCachedConstant(
      final ThrowingSupplier<? extends T> supplier
    ){
      return closeableCachedConstant(supplier, AutoCloseable::close);
    }

  public static final <T> CloseableCachedConstant<T> closeableCachedConstant(
      final ThrowingSupplier<? extends T> supplier, final ThrowingConsumer<? super T> closer
    ){
      return new CloseableConstantImp<>(supplier, closer);
    }

  /**
   * Cache algorithm that makes use of the constant character by unsynchronized read access to a instance variable.
   * (no "out-of-the-air values" implies: if the value is not null, it is correct.)
   */
  private static abstract class AbstractCachedConstant<E> implements CachedConstant<E>{

    protected final Object lock = new Object();

    @Override
    public final E get() {
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



    @Override
    public final void set(final E value) {
      synchronized(lock){
        final E current = getIntern();
        if(current==null){
          setIntern(value);
        }else{
          verifyEqual(value, current);
        }
      }
    }



    protected abstract E retrieveValue();

    protected abstract E getIntern();

    protected abstract void setIntern(E value);
  }

  private static class SoftRefConstant<T> extends AbstractCachedConstant<T>{
    private final Constant<? extends T> supplier;
    SoftReference<T> ref;
    private SoftRefConstant(final Constant<? extends T> supplier) {
      this.supplier = supplier;
    }
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
  }

  private static class CloseableConstantImp<T>
  extends AbstractCachedConstant<T> implements CloseableCachedConstant<T>{
    private T value;
    private ThrowingSupplier<? extends T> supplier;
    private boolean closed = false;
    private final ThrowingConsumer<? super T> closer;
    private CloseableConstantImp(
      final ThrowingSupplier<? extends T> supplier, final ThrowingConsumer<? super T> closer
    ){
      this.supplier = supplier;
      this.closer = closer;
    }
    @Override
    protected void setIntern(final T value) {
      assert this.value==null && value!=null;
      this.value = value;
    }
    @Override
    protected T getIntern() {
      if(closed) throw new IllegalStateException("Closed.");
      return value;
    }
    @Override
    protected T retrieveValue() {
      final T value = call(supplier::get);
      supplier = null;
      return value;
    }
    @Override
    public void close() {
      if(!closed){
        synchronized(lock){
          if(value!=null) run(()->closer.accept(value));
          closed = true;
          value = null;
        }
      }
    }
    @Override
    public boolean closed() {
      return closed;
    }
  }
}
