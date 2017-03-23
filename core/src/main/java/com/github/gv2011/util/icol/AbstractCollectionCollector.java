package com.github.gv2011.util.icol;

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
