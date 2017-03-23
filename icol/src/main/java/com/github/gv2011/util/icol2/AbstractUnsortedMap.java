package com.github.gv2011.util.icol2;

import java.util.Iterator;

import com.github.gv2011.util.icol.ICollection;
import com.github.gv2011.util.icol.IIterator;
import com.github.gv2011.util.icol.ISet;

abstract class AbstractUnsortedMap<K,V> extends AbstractMap<K,V>{

  protected AbstractUnsortedMap() {
    super();
  }

  /**
   * Each of these fields are initialized to contain an instance of the
   * appropriate view the first time this view is requested.  The views are
   * stateless, so there's no reason to create more than one of each.
   */
  transient volatile ISet<K>  keySet = null;
  transient volatile ICollection<V> values = null;

  @Override
  public ISet<K> keySet() {
      if (keySet == null) {
          keySet = new AbstractSet<K>() {
              @Override
              public IIterator<K> iterator() {
                  return new IIterator<K>() {
                      private final Iterator<Entry<K,V>> i = entrySet().iterator();

                      @Override
                      public boolean hasNext() {
                          return i.hasNext();
                      }

                      @Override
                      public K next() {
                          return i.next().getKey();
                      }
                  };
              }

              @Override
              public int size() {
                  return AbstractUnsortedMap.this.size();
              }

              @Override
              public boolean isEmpty() {
                  return AbstractUnsortedMap.this.isEmpty();
              }

              @Override
              public boolean contains(final Object k) {
                  return AbstractUnsortedMap.this.containsKey(k);
              }
          };
      }
      return keySet;
  }

  @Override
  public ICollection<V> values() {
      if (values == null) {
          values = new AbstractCollection<V>() {
              @Override
              public IIterator<V> iterator() {
                  return new IIterator<V>() {
                      private final IIterator<Entry<K,V>> i = entrySet().iterator();

                      @Override
                      public boolean hasNext() {
                          return i.hasNext();
                      }

                      @Override
                      public V next() {
                          return i.next().getValue();
                      }

                      @Override
                      public void remove() {
                          i.remove();
                      }
                  };
              }

              @Override
              public int size() {
                  return AbstractUnsortedMap.this.size();
              }

              @Override
              public boolean isEmpty() {
                  return AbstractUnsortedMap.this.isEmpty();
              }

              @Override
              public boolean contains(final Object v) {
                  return AbstractUnsortedMap.this.containsValue(v);
              }
          };
      }
      return values;
  }


}
