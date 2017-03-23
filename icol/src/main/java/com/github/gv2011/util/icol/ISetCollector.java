package com.github.gv2011.util.icol;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

final class ISetCollector<T> implements Collector<T,Set<T>,ISet<T>> {

  @Override
  public Supplier<Set<T>> supplier() {
    return HashSet::new;
  }

  @Override
  public BiConsumer<Set<T>, T> accumulator() {
     return (b,e)->b.add(e);
  }

  @Override
  public BinaryOperator<Set<T>> combiner() {
    return (b1,b2)->{b1.addAll(b2); return b1;};
  }

  @Override
  public Function<Set<T>, ISet<T>> finisher() {
    return s->{
      final int size = s.size();
      if(size==0) return EmptyISet.empty();
      else if(size==1) return new SingleISet<>(s.iterator().next());
      else return new ISetImp<>(s);
    };
  }

  @Override
  public Set<Characteristics> characteristics() {
    return EnumSet.of(Characteristics.UNORDERED);
  }

}
