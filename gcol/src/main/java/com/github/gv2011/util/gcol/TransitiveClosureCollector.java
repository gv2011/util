package com.github.gv2011.util.gcol;

import static com.github.gv2011.util.icol.ICollections.toISet;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.icol.ISet;

final class TransitiveClosureCollector<T>
implements Collector<T, Set<T>, ISet<T>>{

  private static final ISet<Characteristics> CHARACTERISTICS =
    new ISetBuilder<Characteristics>().add(Characteristics.CONCURRENT).add(Characteristics.UNORDERED).build()
  ;

  private final Function<T,Stream<T>> dependents;

  TransitiveClosureCollector(final Function<T,Stream<T>> dependents) {
    this.dependents = dependents;
  }


  @Override
  public Supplier<Set<T>> supplier() {
    return ()->Collections.synchronizedSet(new HashSet<>());
  }


  @Override
  public Set<Characteristics> characteristics() {
    return CHARACTERISTICS;
  }


  @Override
  public BiConsumer<Set<T>, T> accumulator() {
    return this::add;
  }

  private void add(final Set<T> set, final T element){
    if(set.add(element)){
      final ISet<T> dps = dependents.apply(element).collect(toISet());
      dps.forEach(d->add(set, d));
    }
  }

  @Override
  public BinaryOperator<Set<T>> combiner() {
    return (b1,b2)->{
      b2.forEach(e->add(b1, e));
      return b1;
    };
  }

  @Override
  public Function<Set<T>, ISet<T>> finisher() {
    return ICollections::setFrom;
  }

}
