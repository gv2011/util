package com.github.gv2011.util.gcol;

import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static java.util.function.Predicate.not;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.gv2011.util.icol.CollectionBuilder;
import com.github.gv2011.util.icol.ICollection;
import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.icol.ISetList;
import com.google.common.collect.ImmutableList;

final class ISetListBuilder<E> implements ISetList.Builder<E>, CollectionBuilder<ISetList<E>,E,ISetList.Builder<E>> {

  final List<E> list = new ArrayList<>();
  final Set<E> set = new HashSet<>();

  ISetListBuilder<E> self(){
    return this;
  }

  @Override
  public ISetListBuilder<E> add(final E element) {
    synchronized(list){
      verify(notNull(element), not(set::contains));
      set.add(element);
      list.add(element);
    }
    return self();
  }

  @Override
  public boolean tryAdd(final E element) {
    synchronized(list){
      final boolean add = set.add(notNull(element));
      if(add) list.add(element);
      return add;
    }
  }

  @Override
  public <F extends E> ISetListBuilder<E> addAll(final Collection<F> elements) {
    synchronized(list){
      verify(elements.stream().noneMatch(e->e==null ? true : set.contains(e)));
      set.addAll(elements);
      list.addAll(elements);
    }
    return self();
  }

  @Override
  public <F extends E> ISetListBuilder<E> addAll(final ICollection<F> elements) {
    return addAll((Collection<F>) elements);
  }

  @Override
  public <F extends E> ISetListBuilder<E> tryAddAll(final ICollection<F> elements) {
    return tryAddAll((Collection<F>)elements);
  }

  @Override
  public <F extends E> ISetListBuilder<E> tryAddAll(final Collection<F> elements) {
    verify(elements.stream().noneMatch(e->e==null));
    synchronized(list){
      elements.forEach(e->{if(set.add(e)) list.add(e);});
    }
    return self();
  }

  @Override
  public final int size() {
    synchronized(list){return list.size();}
  }

  @Override
  public final E get(final int index) {
    synchronized(list){return list.get(index);}
  }

  @Override
  public final E set(final int index, final E element) {
    synchronized(list){
      verify(!set.contains(notNull(element)) || list.get(index).equals(element));
      return list.set(index, element);
    }
  }

  @Override
  public ISetList<E> build() {
    synchronized(list){
      assert set.size() == list.size();
      if(list.isEmpty()) return ICollections.emptyList();
      return new ISetListWrapper<>(ImmutableList.copyOf(list));
    }
  }

  @Override
  public void insert(final int index, final E element) {
    synchronized(list){
      verify(set.add(notNull(element)));
      list.add(index, element);
    }
  }

}
