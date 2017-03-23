package com.github.gv2011.util.icol2;

import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.util.Optional;

import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;

public class ArraySortedMap<K,V> extends AbstractSortedMap<K,V>{

  private final K[] keys;
  private final V[] values;
  private final boolean reverse;

  private ArraySortedMap(final K[] keys, final V[] values, final boolean reverse) {
    this.keys = keys;
    this.values = values;
    assert keys.length == values.length && keys.length>0;
    this.reverse = reverse;
  }

  @Override
  public ISortedSet<K> keySet() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public IList<V> values() {
    return new ArrayList<>(values);
  }

  @Override
  public Optional<K> tryGetFirstKey() {
    return Optional.of(keys[0]);
  }

  @Override
  public ISortedMap<K, V> descendingMap() {
    return new ArraySortedMap<>(keys, values, !reverse);
  }

  @Override
  public ISortedMap<K, V> subMap(final K fromKey, final boolean fromInclusive, final K toKey, final boolean toInclusive) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public ISet<java.util.Map.Entry<K, V>> entrySet() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

}
