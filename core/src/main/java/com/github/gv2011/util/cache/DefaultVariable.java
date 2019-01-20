package com.github.gv2011.util.cache;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2019 Vinz (https://github.com/gv2011)
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
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gv2011.util.icol.Opt;

final class DefaultVariable<T> implements Variable<T>{

  private static final Logger LOG = LoggerFactory.getLogger(DefaultVariable.class);

  private final Object lock = new Object();
  private boolean updating = false;
  private boolean invalidating = false;
  private boolean valueIsValid = false;
  private Opt<T> value = Opt.empty();
  private final List<Invalidator> invalidators = new ArrayList<>();
  private final Function<Invalidator,T> calculator;



  DefaultVariable(final Function<Invalidator,T> calculator) {
    this.calculator = calculator;
  }

  @Override
  public T get(final Invalidator invalidator) {
    return getInternal(Opt.of(invalidator));
  }

  @Override
  public T get() {
    return getInternal(Opt.empty());
  }

  @Override
  public Opt<T> getLatest() {
    synchronized(lock){
      return value;
    }
  }



  private T getInternal(final Opt<Invalidator> invalidator) {
    Opt<T> result = Opt.empty();
    while(!result.isPresent()){
      boolean updateNeeded;
      synchronized(lock){
        while(updating || invalidating) call(()->lock.wait());
        if(valueIsValid){
          verify(value.isPresent());
          updateNeeded = false;
          invalidator.ifPresent(invalidators::add);
          result = value;
        }
        else{
          verify(invalidators.isEmpty());
          updateNeeded = true;
          updating = true;
        }
      }
      try{
        while(updateNeeded){
          final Setter newSetter = new Setter();
          final T tmpResult = calculator.apply(newSetter);
          synchronized(lock){
            verify(updating && !invalidating);
            verify(invalidators.isEmpty());
            verify(value.isEmpty());
            if(newSetter.isValid){
              value = Opt.of(tmpResult);
              valueIsValid = true;
              updateNeeded = false;
            }
          }
        }
      }
      finally{
        synchronized(lock){
          updating = false;
          lock.notifyAll();
        }
      }
    }
    return result.get();
  }

  private void doInvalidate(){
    boolean invalidationNeeded = true;
    try{
      while(invalidationNeeded){
        Opt<Invalidator> current;
        synchronized(lock){
          verify(invalidating && !updating);
          current = invalidators.isEmpty() ? Opt.empty() : Opt.of(invalidators.get(0));
          if(!current.isPresent()){
            invalidationNeeded = false;
          }
          current.ifPresent(i->{
            try{i.invalidate();}
            catch(final Exception e){
              LOG.error(format("Could not invalidate {}.", i), e);
            }
            synchronized(lock){
              invalidators.remove(i);
            }
          });
        }
      }
    }
    finally{
      synchronized(lock){
        invalidating = false;
        lock.notifyAll();
      }
    }

  }

  private final class Setter implements Invalidator{

    private boolean isValid = true;

    @Override
    public void invalidate() {
      boolean invalidationNeeded = false;
      synchronized(lock){
        if(isValid){
          isValid = false;
          valueIsValid = false;
          if(!invalidators.isEmpty()){
            verify(!updating);
            verify(!invalidating);
            invalidationNeeded = true;
            invalidating = true;
          }
        }
      }
      if(invalidationNeeded) doInvalidate();
    }

  }

}
