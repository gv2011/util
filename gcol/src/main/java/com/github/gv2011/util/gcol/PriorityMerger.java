package com.github.gv2011.util.gcol;

import static com.github.gv2011.util.CollectionUtils.intStream;
import static com.github.gv2011.util.ex.Exceptions.format;

import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISortedMap;
import com.google.common.collect.ImmutableSet;

final class PriorityMerger {


  static <K extends Comparable<? super K>, V> ISortedMap<K, V> priorityMerge(
      final IList<Stream<? extends V>> sources,
      final Function<? super V, ? extends K> key,
      final BinaryOperator<V> mergeFunction
  ) {
    return intStream(sources.size()).parallel().unordered().mapToObj(i->i)
      .flatMap(i->sources.get(i).map(v->new PrioValue<V>(-i,v)))
      .collect(new PriorityCollector<K,V>(key, mergeFunction))
    ;
  }

  private static class PrioValue<V>{
    private final int priority;
    private final V value;
    private PrioValue(final int priority, final V value) {
      this.priority = priority;
      this.value = value;
    }
  }

  private static class PriorityCollector<K extends Comparable<? super K>, V>
  implements Collector<PrioValue<V>, TreeMap<K, PrioValue<V>>, ISortedMap<K, V>>{
    private final Function<? super V, ? extends K> key;
    private final BinaryOperator<V> mergeFunction;
    private PriorityCollector(final Function<? super V, ? extends K> key, final BinaryOperator<V> mergeFunction) {
      this.key = key;
      this.mergeFunction = mergeFunction;
    }
    @Override
    public Supplier<TreeMap<K, PrioValue<V>>> supplier() {
      return TreeMap::new;
    }
    @Override
    public BiConsumer<TreeMap<K, PrioValue<V>>, PrioValue<V>> accumulator() {
      return (map, pv)->{
        map.merge(key.apply(pv.value), pv, this::merge);
      };
    }
    @Override
    public BinaryOperator<TreeMap<K, PrioValue<V>>> combiner() {
      return (m0,m1)->{
        for(final Entry<K, PrioValue<V>> e: m1.entrySet()){
          m0.merge(e.getKey(), e.getValue(), this::merge);
        }
        return m0;
      };
    }
    private PrioValue<V> merge(final PrioValue<V> pv0, final PrioValue<V> pv1){
      final PrioValue<V> result;
      if(pv0.priority==pv1.priority){
        if(pv0.value.equals(pv1.value)){
          result = pv0;
        }else{
          throw new IllegalArgumentException(format(
            "The source collection with index {} contains two elements for key {}.",
            -pv0.priority, key.apply(pv0.value)
          ));
        }
      }
      else{
        if(pv0.priority > pv1.priority){
          result = new PrioValue<V>(pv0.priority, mergeFunction.apply(pv0.value, pv1.value));
        }
        else{
          result = new PrioValue<V>(pv1.priority, mergeFunction.apply(pv1.value, pv0.value));
        }
      }
      return result;
    }
    @Override
    public Function<TreeMap<K, PrioValue<V>>, ISortedMap<K, V>> finisher() {
      return m->new ISortedMapWrapper<>(unpack(m));
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private TreeMap<K,V> unpack(final TreeMap<K, PrioValue<V>> map){
      map.entrySet().stream().unordered().forEach(e->{((Entry)e).setValue(e.getValue().value);});
      return (TreeMap) map;
    }
    @Override
    public Set<Characteristics> characteristics() {
      return ImmutableSet.of();
    }
  }
}
