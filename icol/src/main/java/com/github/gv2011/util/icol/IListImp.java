package com.github.gv2011.util.icol;

import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;

import com.github.gv2011.util.Pair;

final class IListImp<E> extends AbstractList<E> implements IList<E>{

  private final E[] elements;

  @SuppressWarnings("unchecked")
  IListImp(final Collection<E> c) {
    this((E[]) c.toArray());
  }

  private IListImp(final E[] elements) {
    this.elements = elements;
  }

  @Override
  public IList<E> subList(final int fromIndex, final int toIndex) {
    return new IListImp<>(Arrays.copyOfRange(elements, fromIndex, toIndex));
  }

  @Override
  public E get(final int index) {
    return elements[index];
  }

  @Override
  public int size() {
    return elements.length;
  }

  @Override
  public Optional<E> asOptional() {
    final int size = elements.length;
    if(size==0) return Optional.empty();
    else if(size==1) return Optional.of(elements[0]);
    else throw new IllegalStateException();
  }

  @Override
  public E single() {
    final int size = elements.length;
    if(size==0) throw new NoSuchElementException();
    else if(size==1) return elements[0];
    else throw new IllegalStateException();
  }

  @Override
  public E first() {
    if(elements.length==0) throw new NoSuchElementException();
    return elements[0];
  }

  @Override
  public Optional<E> tryGetFirst() {
    if(elements.length==0) return Optional.empty();
    else return Optional.of(elements[0]);
  }

  @Override
  public Map<Integer, E> asMap() {
    return new MapView(asSet());
  }

  private Set<Entry<Integer, E>> asSet() {
    return new SetView();
  }

  private final class MapView extends AbstractMap<Integer, E> {
    private final Set<Entry<Integer, E>> entrySet;
    private MapView(final Set<java.util.Map.Entry<Integer, E>> entrySet) {
      this.entrySet = entrySet;
    }
    @Override
    public Set<Entry<Integer, E>> entrySet() {
      return entrySet;
    }
  }

  private final class SetView extends AbstractSet<Entry<Integer, E>> {
    @Override
    public Iterator<Entry<Integer, E>> iterator() {
      return new MappingIterator<>(new IntIterator(elements.length), i->new Pair<>(i, elements[i]));
    }
    @Override
    public int size() {
      return elements.length;
    }
    @Override
    public Spliterator<Entry<Integer, E>> spliterator() {
      return Spliterators.spliterator(this, Spliterator.DISTINCT);
    }
  }


}
