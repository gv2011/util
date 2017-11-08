package com.github.gv2011.util.icol;

/*-
 * %---license-start---
 * The MIT License (MIT)
 * %
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %
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
 * %---license-end---
 */



import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

public abstract class AbstractCollectionCollector<C extends ICollection<E>,E,B extends CollectionBuilder<C,E,B>>
implements Collector<E, B, C> {

  public static final boolean TRY_ADD = true;
  public static final boolean ADD = !TRY_ADD;

  private final boolean tryAdd;

  protected AbstractCollectionCollector(final boolean tryAdd){
    this.tryAdd = tryAdd;
  }

  @Override
  public BiConsumer<B, E> accumulator() {
    if(tryAdd){
      return (b,e)->{synchronized(b){b.tryAdd(e);}};
    }else{
      return (b,e)->{synchronized(b){b.add(e);}};
    }

  }

  @Override
  public BinaryOperator<B> combiner() {
    if(tryAdd){
      return (b1,b2)->{
        final C s2;
        synchronized(b2){s2 = b2.build();}
        synchronized(b1){b1.tryAddAll(s2);}
        return b1;
      };
    }else{
      return (b1,b2)->{
        final C s2;
        synchronized(b2){s2 = b2.build();}
        synchronized(b1){b1.addAll(s2);}
        return b1;
      };
    }
  }

  @Override
  public Function<B, C> finisher() {
    return b->{
      synchronized(b){return b.build();}
    };
  }

}
