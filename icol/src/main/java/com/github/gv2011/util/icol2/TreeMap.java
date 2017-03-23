package com.github.gv2011.util.icol2;

import static com.github.gv2011.util.Constants.newCachedConstant;
import static com.github.gv2011.util.SetUtils.immutableSetOf;
import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.icol.IIterator;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedSet;
import com.github.gv2011.util.icol2.ArrayList;

/**
 * A Red-Black tree based {@link NavigableMap} implementation.
 * The map is sorted according to the {@linkplain Comparable natural
 * ordering} of its keys, or by a {@link Comparator} provided at map
 * creation time, depending on which constructor is used.
 *
 * <p>This implementation provides guaranteed log(n) time cost for the
 * {@code containsKey}, {@code get}, {@code put} and {@code remove}
 * operations.  Algorithms are adaptations of those in Cormen, Leiserson, and
 * Rivest's <em>Introduction to Algorithms</em>.
 *
 * <p>Note that the ordering maintained by a tree map, like any sorted map, and
 * whether or not an explicit comparator is provided, must be <em>consistent
 * with {@code equals}</em> if this sorted map is to correctly implement the
 * {@code Map} interface.  (See {@code Comparable} or {@code Comparator} for a
 * precise definition of <em>consistent with equals</em>.)  This is so because
 * the {@code Map} interface is defined in terms of the {@code equals}
 * operation, but a sorted map performs all key comparisons using its {@code
 * compareTo} (or {@code compare}) method, so two keys that are deemed equal by
 * this method are, from the standpoint of the sorted map, equal.  The behavior
 * of a sorted map <em>is</em> well-defined even if its ordering is
 * inconsistent with {@code equals}; it just fails to obey the general contract
 * of the {@code Map} interface.
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a map concurrently, and at least one of the
 * threads modifies the map structurally, it <em>must</em> be synchronized
 * externally.  (A structural modification is any operation that adds or
 * deletes one or more mappings; merely changing the value associated
 * with an existing key is not a structural modification.)  This is
 * typically accomplished by synchronizing on some object that naturally
 * encapsulates the map.
 * If no such object exists, the map should be "wrapped" using the
 * {@link Collections#synchronizedSortedMap Collections.synchronizedSortedMap}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the map: <pre>
 *   SortedMap m = Collections.synchronizedSortedMap(new TreeMap(...));</pre>
 *
 * <p>The iterators returned by the {@code iterator} method of the collections
 * returned by all of this class's "collection view methods" are
 * <em>fail-fast</em>: if the map is structurally modified at any time after
 * the iterator is created, in any way except through the iterator's own
 * {@code remove} method, the iterator will throw a {@link
 * ConcurrentModificationException}.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the future.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:   <em>the fail-fast behavior of iterators
 * should be used only to detect bugs.</em>
 *
 * <p>All {@code Map.Entry} pairs returned by methods in this class
 * and its views represent snapshots of mappings at the time they were
 * produced. They do <strong>not</strong> support the {@code Entry.setValue}
 * method. (Note however that it is possible to change mappings in the
 * associated map using {@code put}.)
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 *
 * @author  Josh Bloch and Doug Lea
 * @see Map
 * @see HashMap
 * @see Hashtable
 * @see Comparable
 * @see Comparator
 * @see Collection
 * @since 1.2
 */

public class TreeMap<K,V> extends AbstractSortedMap<K,V> implements ISortedMap<K,V> {

  private final Constant<IList<V>> values = newCachedConstant(this::createValueList);
  private volatile EntrySet entrySet = null;
  private volatile KeySet<K> navigableKeySet = null;
  private volatile ISortedMap<K,V> descendingMap = null;

    /**
     * The comparator used to maintain order in this tree map, or
     * null if it uses the natural ordering of its keys.
     */
    private final @Nullable Comparator<? super K> comparator;

    private final Entry<K,V> root;

    /**
     * The number of entries in the tree
     */
    private final int size;





    TreeMap(@Nullable final Comparator<? super K> comparator, final Entry<K, V> root, final int size) {
      this.comparator = comparator;
      this.root = root;
      this.size = size;
      assert size>0;
    }

    private final IList<V> createValueList(){
      @SuppressWarnings("unchecked")
      final V[] array = (V[]) new Object[size];
      Entry<K, V> entry = getFirstEntry();
      for(int i=0; i<size; i++){
        array[i] = entry.value;
        entry = successor(entry);
      }
      return new ArrayList<>(array);
    }

    // Query Operations

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns {@code true} if this map contains a mapping for the specified
     * key.
     *
     * @param key key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the
     *         specified key
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    @Override
    public boolean containsKey(final Object key) {
        return getEntry(key) != null;
    }

    /**
     * Returns {@code true} if this map maps one or more keys to the
     * specified value.  More formally, returns {@code true} if and only if
     * this map contains at least one mapping to a value {@code v} such
     * that {@code (value==null ? v==null : value.equals(v))}.  This
     * operation will probably require time linear in the map size for
     * most implementations.
     *
     * @param value value whose presence in this map is to be tested
     * @return {@code true} if a mapping to {@code value} exists;
     *         {@code false} otherwise
     * @since 1.2
     */
    @Override
    public boolean containsValue(final Object value) {
        for (Entry<K,V> e = getFirstEntry(); e != null; e = successor(e))
            if (valEquals(value, e.value))
                return true;
        return false;
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code key} compares
     * equal to {@code k} according to the map's ordering, then this
     * method returns {@code v}; otherwise it returns {@code null}.
     * (There can be at most one such mapping.)
     *
     * <p>A return value of {@code null} does not <em>necessarily</em>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     * The {@link #containsKey containsKey} operation may be used to
     * distinguish these two cases.
     *
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    @Override
    public V get(final Object key) {
        final Entry<K,V> p = getEntry(key);
        if(p==null) throw new NoSuchElementException();
        return p.value;
    }

    @Override
    public Optional<V> tryGet(final Object key) {
        final Entry<K,V> p = getEntry(key);
        return p==null?Optional.empty():Optional.of(p.value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Comparator<? super K> comparator() {
        return comparator==null ? (Comparator<K>)Comparator.naturalOrder() : comparator;
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    @Override
    public K firstKey() {
      return key(getFirstEntry());
    }
    @Override
    public Optional<K> tryGetFirstKey() {
      return optKey(getFirstEntry());
    }


    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    @Override
    public K lastKey() {
        return key(getLastEntry());
    }

    /**
     * Returns this map's entry for the given key, or {@code null} if the map
     * does not contain an entry for the key.
     *
     * @return this map's entry for the given key, or {@code null} if the map
     *         does not contain an entry for the key
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    final Entry<K,V> getEntry(final Object key) {
        // Offload comparator-based version for sake of performance
        if (comparator != null)
            return getEntryUsingComparator(key);
        if (key == null)
            throw new NullPointerException();
        @SuppressWarnings("unchecked")
        final
            Comparable<? super K> k = (Comparable<? super K>) key;
        Entry<K,V> p = root;
        while (p != null) {
            final int cmp = k.compareTo(p.key);
            if (cmp < 0)
                p = p.left;
            else if (cmp > 0)
                p = p.right;
            else
                return p;
        }
        return null;
    }

    /**
     * Version of getEntry using comparator. Split off from getEntry
     * for performance. (This is not worth doing for most methods,
     * that are less dependent on comparator performance, but is
     * worthwhile here.)
     */
    final @Nullable Entry<K,V> getEntryUsingComparator(final Object key) {
        @SuppressWarnings("unchecked")
        final K k = (K) key;
        final Comparator<? super K> cpr = comparator;
        if (cpr != null) {
            Entry<K,V> p = root;
            while (p != null) {
                final int cmp = cpr.compare(k, p.key);
                if (cmp < 0)      p = p.left;
                else if (cmp > 0) p = p.right;
                else return p;
            }
        }
        return null;
    }

    /**
     * Gets the entry corresponding to the specified key; if no such entry
     * exists, returns the entry for the least key greater than the specified
     * key; if no such entry exists (i.e., the greatest key in the Tree is less
     * than the specified key), returns {@code null}.
     */
    final Entry<K,V> getCeilingEntry(final K key) {
        Entry<K,V> p = root;
        while (p != null) {
            final int cmp = compare(key, p.key);
            if (cmp < 0) {
                if (p.left != null)
                    p = p.left;
                else
                    return p;
            } else if (cmp > 0) {
                if (p.right != null) {
                    p = p.right;
                } else {
                    Entry<K,V> parent = p.parent;
                    Entry<K,V> ch = p;
                    while (parent != null && ch == parent.right) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            } else
                return p;
        }
        return null;
    }

    /**
     * Gets the entry corresponding to the specified key; if no such entry
     * exists, returns the entry for the greatest key less than the specified
     * key; if no such entry exists, returns {@code null}.
     */
    final Entry<K,V> getFloorEntry(final K key) {
        Entry<K,V> p = root;
        while (p != null) {
            final int cmp = compare(key, p.key);
            if (cmp > 0) {
                if (p.right != null)
                    p = p.right;
                else
                    return p;
            } else if (cmp < 0) {
                if (p.left != null) {
                    p = p.left;
                } else {
                    Entry<K,V> parent = p.parent;
                    Entry<K,V> ch = p;
                    while (parent != null && ch == parent.left) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            } else
                return p;

        }
        return null;
    }

    /**
     * Gets the entry for the least key greater than the specified
     * key; if no such entry exists, returns the entry for the least
     * key greater than the specified key; if no such entry exists
     * returns {@code null}.
     */
    final Entry<K,V> getHigherEntry(final K key) {
        Entry<K,V> p = root;
        while (p != null) {
            final int cmp = compare(key, p.key);
            if (cmp < 0) {
                if (p.left != null)
                    p = p.left;
                else
                    return p;
            } else {
                if (p.right != null) {
                    p = p.right;
                } else {
                    Entry<K,V> parent = p.parent;
                    Entry<K,V> ch = p;
                    while (parent != null && ch == parent.right) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            }
        }
        return null;
    }

    /**
     * Returns the entry for the greatest key less than the specified key; if
     * no such entry exists (i.e., the least key in the Tree is greater than
     * the specified key), returns {@code null}.
     */
    final Entry<K,V> getLowerEntry(final K key) {
        Entry<K,V> p = root;
        while (p != null) {
            final int cmp = compare(key, p.key);
            if (cmp > 0) {
                if (p.right != null)
                    p = p.right;
                else
                    return p;
            } else {
                if (p.left != null) {
                    p = p.left;
                } else {
                    Entry<K,V> parent = p.parent;
                    Entry<K,V> ch = p;
                    while (parent != null && ch == parent.left) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            }
        }
        return null;
    }


    // NavigableMap API methods

    /**
     * @since 1.6
     */
    @Override
    public Map.Entry<K,V> firstEntry() {
        return exportEntry(getFirstEntry());
    }

    /**
     * @since 1.6
     */
    @Override
    public Map.Entry<K,V> lastEntry() {
        return exportEntry(getLastEntry());
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @since 1.6
     */
    @Override
    public Map.Entry<K,V> lowerEntry(final K key) {
        return exportEntry(getLowerEntry(key));
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @since 1.6
     */
    @Override
    public K lowerKey(final K key) {
        return keyOrNull(getLowerEntry(key));
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @since 1.6
     */
    @Override
    public Map.Entry<K,V> floorEntry(final K key) {
        return exportEntry(getFloorEntry(key));
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @since 1.6
     */
    @Override
    public K floorKey(final K key) {
        return keyOrNull(getFloorEntry(key));
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @since 1.6
     */
    @Override
    public Map.Entry<K,V> ceilingEntry(final K key) {
        return exportEntry(getCeilingEntry(key));
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @since 1.6
     */
    @Override
    public K ceilingKey(final K key) {
        return keyOrNull(getCeilingEntry(key));
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @since 1.6
     */
    @Override
    public Map.Entry<K,V> higherEntry(final K key) {
        return exportEntry(getHigherEntry(key));
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @since 1.6
     */
    @Override
    public K higherKey(final K key) {
        return keyOrNull(getHigherEntry(key));
    }

    // Views

    /**
     * Fields initialized to contain an instance of the entry set view
     * the first time this view is requested.  Views are stateless, so
     * there's no reason to create more than one.
     */

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     *
     * <p>The set's iterator returns the keys in ascending order.
     * The set's spliterator is
     * <em><a href="Spliterator.html#binding">late-binding</a></em>,
     * <em>fail-fast</em>, and additionally reports {@link Spliterator#SORTED}
     * and {@link Spliterator#ORDERED} with an encounter order that is ascending
     * key order.  The spliterator's comparator (see
     * {@link java.util.Spliterator#getComparator()}) is {@code null} if
     * the tree map's comparator (see {@link #comparator()}) is {@code null}.
     * Otherwise, the spliterator's comparator is the same as or imposes the
     * same total ordering as the tree map's comparator.
     *
     * <p>The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own {@code remove} operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * {@code Iterator.remove}, {@code Set.remove},
     * {@code removeAll}, {@code retainAll}, and {@code clear}
     * operations.  It does not support the {@code add} or {@code addAll}
     * operations.
     */
    @Override
    public ISortedSet<K> keySet() {
        return navigableKeySet();
    }

    /**
     * @since 1.6
     */
    @Override
    public ISortedSet<K> navigableKeySet() {
        final KeySet<K> nks = navigableKeySet;
        return (nks != null) ? nks : (navigableKeySet = new KeySet<>(this));
    }

    /**
     * @since 1.6
     */
    @Override
    public ISortedSet<K> descendingKeySet() {
        return descendingMap().navigableKeySet();
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     *
     * <p>The collection's iterator returns the values in ascending order
     * of the corresponding keys. The collection's spliterator is
     * <em><a href="Spliterator.html#binding">late-binding</a></em>,
     * <em>fail-fast</em>, and additionally reports {@link Spliterator#ORDERED}
     * with an encounter order that is ascending order of the corresponding
     * keys.
     *
     * <p>The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own {@code remove} operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the {@code Iterator.remove},
     * {@code Collection.remove}, {@code removeAll},
     * {@code retainAll} and {@code clear} operations.  It does not
     * support the {@code add} or {@code addAll} operations.
     */
    @Override
    public IList<V> values() {
      return values.get();
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     *
     * <p>The set's iterator returns the entries in ascending key order. The
     * sets's spliterator is
     * <em><a href="Spliterator.html#binding">late-binding</a></em>,
     * <em>fail-fast</em>, and additionally reports {@link Spliterator#SORTED} and
     * {@link Spliterator#ORDERED} with an encounter order that is ascending key
     * order.
     *
     * <p>The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own {@code remove} operation, or through the
     * {@code setValue} operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the {@code Iterator.remove},
     * {@code Set.remove}, {@code removeAll}, {@code retainAll} and
     * {@code clear} operations.  It does not support the
     * {@code add} or {@code addAll} operations.
     */
    @Override
    public ISet<Map.Entry<K,V>> entrySet() {
        final EntrySet es = entrySet;
        return (es != null) ? es : (entrySet = new EntrySet());
    }

    /**
     * @since 1.6
     */
    @Override
    public ISortedMap<K, V> descendingMap() {
        final ISortedMap<K, V> km = descendingMap;
        return (km != null) ? km :
            (descendingMap = new DescendingSubMap<>(this,
                                                    true, null, true,
                                                    true, null, true));
    }

    /**
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException if {@code fromKey} or {@code toKey} is
     *         null and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @throws IllegalArgumentException {@inheritDoc}
     * @since 1.6
     */
    @Override
    public ISortedMap<K,V> subMap(final K fromKey, final boolean fromInclusive,
                                    final K toKey,   final boolean toInclusive) {
        return new AscendingSubMap<>(this,
                                     false, fromKey, fromInclusive,
                                     false, toKey,   toInclusive);
    }

    /**
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException if {@code toKey} is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @throws IllegalArgumentException {@inheritDoc}
     * @since 1.6
     */
    @Override
    public ISortedMap<K,V> headMap(final K toKey, final boolean inclusive) {
        return new AscendingSubMap<>(this,
                                     true,  null,  true,
                                     false, toKey, inclusive);
    }

    /**
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException if {@code fromKey} is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @throws IllegalArgumentException {@inheritDoc}
     * @since 1.6
     */
    @Override
    public ISortedMap<K,V> tailMap(final K fromKey, final boolean inclusive) {
        return new AscendingSubMap<>(this,
                                     false, fromKey, inclusive,
                                     true,  null,    true);
    }

    /**
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException if {@code fromKey} or {@code toKey} is
     *         null and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public ISortedMap<K,V> subMap(final K fromKey, final K toKey) {
        return subMap(fromKey, true, toKey, false);
    }

    /**
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException if {@code toKey} is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public ISortedMap<K,V> headMap(final K toKey) {
        return headMap(toKey, false);
    }

    /**
     * @throws ClassCastException       {@inheritDoc}
     * @throws NullPointerException if {@code fromKey} is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public ISortedMap<K,V> tailMap(final K fromKey) {
        return tailMap(fromKey, true);
    }

    @Override
    public void forEach(final BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        for (Entry<K, V> e = getFirstEntry(); e != null; e = successor(e)) {
            action.accept(e.key, e.value);
        }
    }

    // View class support


    class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        @Override
        public IIterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator(getFirstEntry());
        }

        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            final Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
            final Object value = entry.getValue();
            final Entry<K,V> p = getEntry(entry.getKey());
            return p != null && valEquals(p.getValue(), value);
        }

        @Override
        public int size() {
            return TreeMap.this.size();
        }

        @Override
        public Spliterator<Map.Entry<K,V>> spliterator() {
            return new EntrySpliterator<>(TreeMap.this, null, null, 0, -1);
        }
    }

    /*
     * Unlike Values and EntrySet, the KeySet class is static,
     * delegating to a NavigableMap to allow use by SubMaps, which
     * outweighs the ugliness of needing type-tests for the following
     * Iterator methods that are defined appropriately in main versus
     * submap classes.
     */

    IIterator<K> keyIterator() {
        return new KeyIterator(getFirstEntry());
    }

    Iterator<K> descendingKeyIterator() {
        return new DescendingKeyIterator(getLastEntry());
    }

    static final class KeySet<E> extends AbstractSortedSet<E>{
        private final ISortedMap<E, ?> m;
        KeySet(final ISortedMap<E,?> map) { m = map; }

        @Override
        public IIterator<E> iterator() {
            if (m instanceof TreeMap)
                return ((TreeMap<E,?>)m).keyIterator();
            else
                return ((TreeMap.NavigableSubMap<E,?>)m).keyIterator();
        }

        @Override
        public Iterator<E> descendingIterator() {
            if (m instanceof TreeMap)
                return ((TreeMap<E,?>)m).descendingKeyIterator();
            else
                return ((TreeMap.NavigableSubMap<E,?>)m).descendingKeyIterator();
        }

        @Override
        public int size() { return m.size(); }
        @Override
        public boolean isEmpty() { return m.isEmpty(); }
        @Override
        public boolean contains(final Object o) { return m.containsKey(o); }

        @Override
        public E lower(final E e) { return m.lowerKey(e); }
        @Override
        public Optional<E> tryGetlower(final E e) {  return m.tryGetLowerKey(e); }

        @Override
        public E floor(final E e) { return m.floorKey(e); }
        @Override
        public Optional<E> tryGetFloor(final E e) { return m.tryGetFloorKey(e); }

        @Override
        public E ceiling(final E e) { return m.ceilingKey(e); }
        @Override
        public Optional<E> tryGetCeiling(final E e) { return m.tryGetCeilingKey(e); }

        @Override
        public E higher(final E e) { return m.higherKey(e); }
        @Override
        public Optional<E> tryGetHigher(final E e) { return m.tryGetHigherKey(e); }

        @Override
        public E first() { return m.firstKey(); }

        @Override
        public Optional<E> tryGetFirst() { return m.tryGetFirstKey(); }

        @Override
        public E last() { return m.lastKey(); }
        @Override
        public Optional<E> tryGetLast(){ return m.tryGetLastKey(); }

        @Override
        public Comparator<? super E> comparator() { return m.comparator(); }

        @Override
        public ISortedSet<E> subSet(final E fromElement, final boolean fromInclusive,
                                      final E toElement,   final boolean toInclusive) {
            return new KeySet<>(m.subMap(fromElement, fromInclusive,
                                          toElement,   toInclusive));
        }
        @Override
        public ISortedSet<E> headSet(final E toElement, final boolean inclusive) {
            return new KeySet<>(m.headMap(toElement, inclusive));
        }
        @Override
        public ISortedSet<E> tailSet(final E fromElement, final boolean inclusive) {
            return new KeySet<>(m.tailMap(fromElement, inclusive));
        }
        @Override
        public ISortedSet<E> subSet(final E fromElement, final E toElement) {
            return subSet(fromElement, true, toElement, false);
        }
        @Override
        public ISortedSet<E> headSet(final E toElement) {
            return headSet(toElement, false);
        }
        @Override
        public ISortedSet<E> tailSet(final E fromElement) {
            return tailSet(fromElement, true);
        }
        @Override
        public ISortedSet<E> descendingSet() {
            return new KeySet<>(m.descendingMap());
        }

        @Override
        public Spliterator<E> spliterator() {
            return keySpliteratorFor(m);
        }

    }

    /**
     * Base class for TreeMap Iterators
     */
    abstract class PrivateEntryIterator<T> implements IIterator<T> {
        Entry<K,V> next;
        Entry<K,V> lastReturned;
//        int expectedModCount;

        PrivateEntryIterator(final Entry<K,V> first) {
//            expectedModCount = modCount;
            lastReturned = null;
            next = first;
        }

        @Override
        public final boolean hasNext() {
            return next != null;
        }

        final Entry<K,V> nextEntry() {
            final Entry<K,V> e = next;
            if (e == null) throw new NoSuchElementException();
            next = successor(e);
            lastReturned = e;
            return e;
        }

        final Entry<K,V> prevEntry() {
            final Entry<K,V> e = next;
            if (e == null)
                throw new NoSuchElementException();
            next = predecessor(e);
            lastReturned = e;
            return e;
        }
    }

    final class EntryIterator extends PrivateEntryIterator<Map.Entry<K,V>> {
        EntryIterator(final Entry<K,V> first) {
            super(first);
        }
        @Override
        public Map.Entry<K,V> next() {
            return nextEntry();
        }
    }

    final class ValueIterator extends PrivateEntryIterator<V> {
        ValueIterator(final Entry<K,V> first) {
            super(first);
        }
        @Override
        public V next() {
            return nextEntry().value;
        }
    }

    final class KeyIterator extends PrivateEntryIterator<K> {
        KeyIterator(final Entry<K,V> first) {
            super(first);
        }
        @Override
        public K next() {
            return nextEntry().key;
        }
    }

    final class DescendingKeyIterator extends PrivateEntryIterator<K> {
        DescendingKeyIterator(final Entry<K,V> first) {
            super(first);
        }
        @Override
        public K next() {
            return prevEntry().key;
        }
    }

    // Little utilities

    /**
     * Compares two keys using the correct comparison method for this TreeMap.
     */
    @SuppressWarnings("unchecked")
    final int compare(final Object k1, final Object k2) {
        return comparator==null ? ((Comparable<? super K>)k1).compareTo((K)k2)
            : comparator.compare((K)k1, (K)k2);
    }

    /**
     * Test two values for equality.  Differs from o1.equals(o2) only in
     * that it copes with {@code null} o1 properly.
     */
    static final boolean valEquals(final Object o1, final Object o2) {
        return (o1==null ? o2==null : o1.equals(o2));
    }

    /**
     * Return SimpleImmutableEntry for entry, or null if null
     */
    static <K,V> Map.Entry<K,V> exportEntry(final TreeMap.Entry<K,V> e) {
        return (e == null) ? null :
            new AbstractMap.SimpleImmutableEntry<>(e);
    }

    /**
     * Return key for entry, or null if null
     */
    static <K,V> K keyOrNull(final TreeMap.Entry<K,V> e) {
        return (e == null) ? null : e.key;
    }

  /**
   * Returns the key corresponding to the specified Entry.
   * @throws NoSuchElementException if the Entry is null
   */
  static final <K> K key(final Entry<K,?> e) {
    if (e==null) throw new NoSuchElementException();
    return e.key;
  }

  static final <K> Optional<K> optKey(final Entry<K,?> e) {
    return e==null?Optional.empty():Optional.of(e.key);
  }


    // SubMaps

    /**
     * Dummy value serving as unmatchable fence key for unbounded
     * SubMapIterators
     */
    private static final Object UNBOUNDED = new Object();

    /**
     * @serial include
     */
    abstract static class NavigableSubMap<K,V> extends AbstractSortedMap<K,V>{
        /**
         * The backing map.
         */
        final TreeMap<K,V> m;

        /**
         * Endpoints are represented as triples (fromStart, lo,
         * loInclusive) and (toEnd, hi, hiInclusive). If fromStart is
         * true, then the low (absolute) bound is the start of the
         * backing map, and the other values are ignored. Otherwise,
         * if loInclusive is true, lo is the inclusive bound, else lo
         * is the exclusive bound. Similarly for the upper bound.
         */
        final K lo, hi;
        final boolean fromStart, toEnd;
        final boolean loInclusive, hiInclusive;

        NavigableSubMap(final TreeMap<K,V> m,
                        final boolean fromStart, final K lo, final boolean loInclusive,
                        final boolean toEnd,     final K hi, final boolean hiInclusive) {
            if (!fromStart && !toEnd) {
                if (m.compare(lo, hi) > 0)
                    throw new IllegalArgumentException("fromKey > toKey");
            } else {
                if (!fromStart) // type check
                    m.compare(lo, lo);
                if (!toEnd)
                    m.compare(hi, hi);
            }

            this.m = m;
            this.fromStart = fromStart;
            this.lo = lo;
            this.loInclusive = loInclusive;
            this.toEnd = toEnd;
            this.hi = hi;
            this.hiInclusive = hiInclusive;
        }

        @Override
        public final Entry<K, V> pollFirstEntry() {
          throw new UnsupportedOperationException();
        }

        @Override
        public final Entry<K, V> pollLastEntry() {
          throw new UnsupportedOperationException();
        }




        // internal utilities

        final boolean tooLow(final Object key) {
            if (!fromStart) {
                final int c = m.compare(key, lo);
                if (c < 0 || (c == 0 && !loInclusive))
                    return true;
            }
            return false;
        }

        final boolean tooHigh(final Object key) {
            if (!toEnd) {
                final int c = m.compare(key, hi);
                if (c > 0 || (c == 0 && !hiInclusive))
                    return true;
            }
            return false;
        }

        final boolean inRange(final Object key) {
            return !tooLow(key) && !tooHigh(key);
        }

        final boolean inClosedRange(final Object key) {
            return (fromStart || m.compare(key, lo) >= 0)
                && (toEnd || m.compare(hi, key) >= 0);
        }

        final boolean inRange(final Object key, final boolean inclusive) {
            return inclusive ? inRange(key) : inClosedRange(key);
        }

        /*
         * Absolute versions of relation operations.
         * Subclasses map to these using like-named "sub"
         * versions that invert senses for descending maps
         */

        final TreeMap.Entry<K,V> absLowest() {
            final TreeMap.Entry<K,V> e =
                (fromStart ?  m.getFirstEntry() :
                 (loInclusive ? m.getCeilingEntry(lo) :
                                m.getHigherEntry(lo)));
            return (e == null || tooHigh(e.key)) ? null : e;
        }

        final TreeMap.Entry<K,V> absHighest() {
            final TreeMap.Entry<K,V> e =
                (toEnd ?  m.getLastEntry() :
                 (hiInclusive ?  m.getFloorEntry(hi) :
                                 m.getLowerEntry(hi)));
            return (e == null || tooLow(e.key)) ? null : e;
        }

        final TreeMap.Entry<K,V> absCeiling(final K key) {
            if (tooLow(key))
                return absLowest();
            final TreeMap.Entry<K,V> e = m.getCeilingEntry(key);
            return (e == null || tooHigh(e.key)) ? null : e;
        }

        final TreeMap.Entry<K,V> absHigher(final K key) {
            if (tooLow(key))
                return absLowest();
            final TreeMap.Entry<K,V> e = m.getHigherEntry(key);
            return (e == null || tooHigh(e.key)) ? null : e;
        }

        final TreeMap.Entry<K,V> absFloor(final K key) {
            if (tooHigh(key))
                return absHighest();
            final TreeMap.Entry<K,V> e = m.getFloorEntry(key);
            return (e == null || tooLow(e.key)) ? null : e;
        }

        final TreeMap.Entry<K,V> absLower(final K key) {
            if (tooHigh(key))
                return absHighest();
            final TreeMap.Entry<K,V> e = m.getLowerEntry(key);
            return (e == null || tooLow(e.key)) ? null : e;
        }

        /** Returns the absolute high fence for ascending traversal */
        final TreeMap.Entry<K,V> absHighFence() {
            return (toEnd ? null : (hiInclusive ?
                                    m.getHigherEntry(hi) :
                                    m.getCeilingEntry(hi)));
        }

        /** Return the absolute low fence for descending traversal  */
        final TreeMap.Entry<K,V> absLowFence() {
            return (fromStart ? null : (loInclusive ?
                                        m.getLowerEntry(lo) :
                                        m.getFloorEntry(lo)));
        }

        // Abstract methods defined in ascending vs descending classes
        // These relay to the appropriate absolute versions

        abstract TreeMap.Entry<K,V> subLowest();
        abstract TreeMap.Entry<K,V> subHighest();
        abstract TreeMap.Entry<K,V> subCeiling(K key);
        abstract TreeMap.Entry<K,V> subHigher(K key);
        abstract TreeMap.Entry<K,V> subFloor(K key);
        abstract TreeMap.Entry<K,V> subLower(K key);

        /** Returns ascending iterator from the perspective of this submap */
        abstract IIterator<K> keyIterator();

        abstract Spliterator<K> keySpliterator();

        /** Returns descending iterator from the perspective of this submap */
        abstract IIterator<K> descendingKeyIterator();

        // public methods

        @Override
        public boolean isEmpty() {
            return (fromStart && toEnd) ? m.isEmpty() : entrySet().isEmpty();
        }

        @Override
        public int size() {
            return (fromStart && toEnd) ? m.size() : entrySet().size();
        }

        @Override
        public final boolean containsKey(final Object key) {
            return inRange(key) && m.containsKey(key);
        }

        @Override
        public final V get(final Object key) {
            return !inRange(key) ? null :  m.get(key);
        }

        @Override
        public final Map.Entry<K,V> ceilingEntry(final K key) {
            return exportEntry(subCeiling(key));
        }

        @Override
        public final K ceilingKey(final K key) {
            return keyOrNull(subCeiling(key));
        }

        @Override
        public final Map.Entry<K,V> higherEntry(final K key) {
            return exportEntry(subHigher(key));
        }

        @Override
        public final K higherKey(final K key) {
            return keyOrNull(subHigher(key));
        }

        @Override
        public final Map.Entry<K,V> floorEntry(final K key) {
            return exportEntry(subFloor(key));
        }

        @Override
        public final K floorKey(final K key) {
            return keyOrNull(subFloor(key));
        }

        @Override
        public final Map.Entry<K,V> lowerEntry(final K key) {
            return exportEntry(subLower(key));
        }

        @Override
        public final K lowerKey(final K key) {
            return keyOrNull(subLower(key));
        }

        @Override
        public final K firstKey() {
            return key(subLowest());
        }

        @Override
        public final K lastKey() {
            return key(subHighest());
        }

        @Override
        public final Map.Entry<K,V> firstEntry() {
            return exportEntry(subLowest());
        }

        @Override
        public final Map.Entry<K,V> lastEntry() {
            return exportEntry(subHighest());
        }

        // Views
        transient ISortedMap<K,V> descendingMapView = null;
        transient EntrySetView entrySetView = null;
        transient KeySet<K> navigableKeySetView = null;

        @Override
        public final ISortedSet<K> navigableKeySet() {
            final KeySet<K> nksv = navigableKeySetView;
            return (nksv != null) ? nksv :
                (navigableKeySetView = new TreeMap.KeySet<>(this));
        }

        @Override
        public final ISortedSet<K> keySet() {
            return navigableKeySet();
        }

        @Override
        public ISortedSet<K> descendingKeySet() {
            return descendingMap().navigableKeySet();
        }

        @Override
        public final ISortedMap<K,V> subMap(final K fromKey, final K toKey) {
            return subMap(fromKey, true, toKey, false);
        }

        @Override
        public final ISortedMap<K,V> headMap(final K toKey) {
            return headMap(toKey, false);
        }

        @Override
        public final ISortedMap<K,V> tailMap(final K fromKey) {
            return tailMap(fromKey, true);
        }

        // View classes

        abstract class EntrySetView extends AbstractSet<Map.Entry<K,V>> {
            private transient int size = -1;

            @Override
            public int size() {
                if (fromStart && toEnd)
                    return m.size();
                if (size == -1) {
                    size = 0;
                    final Iterator<?> i = iterator();
                    while (i.hasNext()) {
                        size++;
                        i.next();
                    }
                }
                return size;
            }

            @Override
            public boolean isEmpty() {
                final TreeMap.Entry<K,V> n = absLowest();
                return n == null || tooHigh(n.key);
            }

            @Override
            public boolean contains(final Object o) {
                if (!(o instanceof Map.Entry))
                    return false;
                final Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
                final Object key = entry.getKey();
                if (!inRange(key))
                    return false;
                final TreeMap.Entry<?,?> node = m.getEntry(key);
                return node != null &&
                    valEquals(node.getValue(), entry.getValue());
            }

        }

        /**
         * Iterators for SubMaps
         */
        abstract class SubMapIterator<T> implements IIterator<T> {
            TreeMap.Entry<K,V> lastReturned;
            TreeMap.Entry<K,V> next;
            final Object fenceKey;

            SubMapIterator(final TreeMap.Entry<K,V> first,
                           final TreeMap.Entry<K,V> fence) {
                lastReturned = null;
                next = first;
                fenceKey = fence == null ? UNBOUNDED : fence.key;
            }

            @Override
            public final boolean hasNext() {
                return next != null && next.key != fenceKey;
            }

            final TreeMap.Entry<K,V> nextEntry() {
                final TreeMap.Entry<K,V> e = next;
                if (e == null || e.key == fenceKey)
                    throw new NoSuchElementException();
                next = successor(e);
                lastReturned = e;
                return e;
            }

            final TreeMap.Entry<K,V> prevEntry() {
                final TreeMap.Entry<K,V> e = next;
                if (e == null || e.key == fenceKey)
                    throw new NoSuchElementException();
                next = predecessor(e);
                lastReturned = e;
                return e;
            }

        }

        final class SubMapEntryIterator extends SubMapIterator<Map.Entry<K,V>> {
            SubMapEntryIterator(final TreeMap.Entry<K,V> first,
                                final TreeMap.Entry<K,V> fence) {
                super(first, fence);
            }
            @Override
            public Map.Entry<K,V> next() {
                return nextEntry();
            }
        }

        final class DescendingSubMapEntryIterator extends SubMapIterator<Map.Entry<K,V>> {
            DescendingSubMapEntryIterator(final TreeMap.Entry<K,V> last,
                                          final TreeMap.Entry<K,V> fence) {
                super(last, fence);
            }

            @Override
            public Map.Entry<K,V> next() {
                return prevEntry();
            }
        }

        // Implement minimal Spliterator as KeySpliterator backup
        final class SubMapKeyIterator extends SubMapIterator<K>
            implements Spliterator<K> {
            SubMapKeyIterator(final TreeMap.Entry<K,V> first,
                              final TreeMap.Entry<K,V> fence) {
                super(first, fence);
            }
            @Override
            public K next() {
                return nextEntry().key;
            }
            @Override
            public Spliterator<K> trySplit() {
                return null;
            }
            @Override
            public void forEachRemaining(final Consumer<? super K> action) {
                while (hasNext())
                    action.accept(next());
            }
            @Override
            public boolean tryAdvance(final Consumer<? super K> action) {
                if (hasNext()) {
                    action.accept(next());
                    return true;
                }
                return false;
            }
            @Override
            public long estimateSize() {
                return Long.MAX_VALUE;
            }
            @Override
            public int characteristics() {
                return Spliterator.DISTINCT | Spliterator.ORDERED |
                    Spliterator.SORTED | Spliterator.SIZED | Spliterator.SUBSIZED;
            }
            @Override
            public final Comparator<? super K>  getComparator() {
                return comparator();
            }
        }

        final class DescendingSubMapKeyIterator extends SubMapIterator<K>
            implements Spliterator<K> {
            DescendingSubMapKeyIterator(final TreeMap.Entry<K,V> last,
                                        final TreeMap.Entry<K,V> fence) {
                super(last, fence);
            }
            @Override
            public K next() {
                return prevEntry().key;
            }
            @Override
            public Spliterator<K> trySplit() {
                return null;
            }
            @Override
            public void forEachRemaining(final Consumer<? super K> action) {
                while (hasNext())
                    action.accept(next());
            }
            @Override
            public boolean tryAdvance(final Consumer<? super K> action) {
                if (hasNext()) {
                    action.accept(next());
                    return true;
                }
                return false;
            }
            @Override
            public long estimateSize() {
                return Long.MAX_VALUE;
            }
            @Override
            public int characteristics() {
                return Spliterator.DISTINCT | Spliterator.ORDERED;
            }
        }
    }

    static final class AscendingSubMap<K,V> extends NavigableSubMap<K,V> {

        AscendingSubMap(final TreeMap<K,V> m,
                        final boolean fromStart, final K lo, final boolean loInclusive,
                        final boolean toEnd,     final K hi, final boolean hiInclusive) {
            super(m, fromStart, lo, loInclusive, toEnd, hi, hiInclusive);
        }

        @Override
        public Comparator<? super K> comparator() {
            return m.comparator();
        }

        @Override
        public ISortedMap<K,V> subMap(final K fromKey, final boolean fromInclusive,
                                        final K toKey,   final boolean toInclusive) {
            if (!inRange(fromKey, fromInclusive))
                throw new IllegalArgumentException("fromKey out of range");
            if (!inRange(toKey, toInclusive))
                throw new IllegalArgumentException("toKey out of range");
            return new AscendingSubMap<>(m,
                                         false, fromKey, fromInclusive,
                                         false, toKey,   toInclusive);
        }

        @Override
        public ISortedMap<K,V> headMap(final K toKey, final boolean inclusive) {
            if (!inRange(toKey, inclusive))
                throw new IllegalArgumentException("toKey out of range");
            return new AscendingSubMap<>(m,
                                         fromStart, lo,    loInclusive,
                                         false,     toKey, inclusive);
        }

        @Override
        public ISortedMap<K,V> tailMap(final K fromKey, final boolean inclusive) {
            if (!inRange(fromKey, inclusive))
                throw new IllegalArgumentException("fromKey out of range");
            return new AscendingSubMap<>(m,
                                         false, fromKey, inclusive,
                                         toEnd, hi,      hiInclusive);
        }

        @Override
        public ISortedMap<K,V> descendingMap() {
            final ISortedMap<K,V> mv = descendingMapView;
            return (mv != null) ? mv :
                (descendingMapView =
                 new DescendingSubMap<>(m,
                                        fromStart, lo, loInclusive,
                                        toEnd,     hi, hiInclusive));
        }

        @Override
        IIterator<K> keyIterator() {
            return new SubMapKeyIterator(absLowest(), absHighFence());
        }

        @Override
        Spliterator<K> keySpliterator() {
            return new SubMapKeyIterator(absLowest(), absHighFence());
        }

        @Override
        IIterator<K> descendingKeyIterator() {
            return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
        }

        final class AscendingEntrySetView extends EntrySetView {
            @Override
            public IIterator<Map.Entry<K,V>> iterator() {
                return new SubMapEntryIterator(absLowest(), absHighFence());
            }
        }

        @Override
        public ISet<Map.Entry<K,V>> entrySet() {
            final EntrySetView es = entrySetView;
            return (es != null) ? es : (entrySetView = new AscendingEntrySetView());
        }

        @Override
        TreeMap.Entry<K,V> subLowest()       { return absLowest(); }
        @Override
        TreeMap.Entry<K,V> subHighest()      { return absHighest(); }
        @Override
        TreeMap.Entry<K,V> subCeiling(final K key) { return absCeiling(key); }
        @Override
        TreeMap.Entry<K,V> subHigher(final K key)  { return absHigher(key); }
        @Override
        TreeMap.Entry<K,V> subFloor(final K key)   { return absFloor(key); }
        @Override
        TreeMap.Entry<K,V> subLower(final K key)   { return absLower(key); }

        @Override
        public IList<V> values() {
          // TODO Auto-generated method stub
          throw notYetImplementedException();
        }

        @Override
        public Optional<K> tryGetFirstKey() {
          // TODO Auto-generated method stub
          throw notYetImplementedException();
        }



    }

    static final class DescendingSubMap<K,V>  extends NavigableSubMap<K,V>{
        DescendingSubMap(final TreeMap<K,V> m,
                        final boolean fromStart, final K lo, final boolean loInclusive,
                        final boolean toEnd,     final K hi, final boolean hiInclusive) {
            super(m, fromStart, lo, loInclusive, toEnd, hi, hiInclusive);
        }

        private final Comparator<? super K> reverseComparator =
            Collections.reverseOrder(m.comparator);

        @Override
        public Comparator<? super K> comparator() {
            return reverseComparator;
        }

        @Override
        public ISortedMap<K,V> subMap(final K fromKey, final boolean fromInclusive,
                                        final K toKey,   final boolean toInclusive) {
            if (!inRange(fromKey, fromInclusive))
                throw new IllegalArgumentException("fromKey out of range");
            if (!inRange(toKey, toInclusive))
                throw new IllegalArgumentException("toKey out of range");
            return new DescendingSubMap<>(m,
                                          false, toKey,   toInclusive,
                                          false, fromKey, fromInclusive);
        }

        @Override
        public ISortedMap<K,V> headMap(final K toKey, final boolean inclusive) {
            if (!inRange(toKey, inclusive))
                throw new IllegalArgumentException("toKey out of range");
            return new DescendingSubMap<>(m,
                                          false, toKey, inclusive,
                                          toEnd, hi,    hiInclusive);
        }

        @Override
        public ISortedMap<K,V> tailMap(final K fromKey, final boolean inclusive) {
            if (!inRange(fromKey, inclusive))
                throw new IllegalArgumentException("fromKey out of range");
            return new DescendingSubMap<>(m,
                                          fromStart, lo, loInclusive,
                                          false, fromKey, inclusive);
        }

        @Override
        public ISortedMap<K,V> descendingMap() {
            final ISortedMap<K,V> mv = descendingMapView;
            return (mv != null) ? mv :
                (descendingMapView =
                 new AscendingSubMap<>(m,
                                       fromStart, lo, loInclusive,
                                       toEnd,     hi, hiInclusive));
        }

        @Override
        IIterator<K> keyIterator() {
            return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
        }

        @Override
        Spliterator<K> keySpliterator() {
            return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
        }

        @Override
        IIterator<K> descendingKeyIterator() {
            return new SubMapKeyIterator(absLowest(), absHighFence());
        }

        final class DescendingEntrySetView extends EntrySetView {
            @Override
            public IIterator<Map.Entry<K,V>> iterator() {
                return new DescendingSubMapEntryIterator(absHighest(), absLowFence());
            }
        }

        @Override
        public ISet<Map.Entry<K,V>> entrySet() {
            final EntrySetView es = entrySetView;
            return (es != null) ? es : (entrySetView = new DescendingEntrySetView());
        }

        @Override
        TreeMap.Entry<K,V> subLowest()       { return absHighest(); }
        @Override
        TreeMap.Entry<K,V> subHighest()      { return absLowest(); }
        @Override
        TreeMap.Entry<K,V> subCeiling(final K key) { return absFloor(key); }
        @Override
        TreeMap.Entry<K,V> subHigher(final K key)  { return absLower(key); }
        @Override
        TreeMap.Entry<K,V> subFloor(final K key)   { return absCeiling(key); }
        @Override
        TreeMap.Entry<K,V> subLower(final K key)   { return absHigher(key); }

        @Override
        public IList<V> values() {
          super.values();
          throw notYetImplementedException();
        }

        @Override
        public Optional<K> tryGetFirstKey() {
          final IIterator<K> kit = keyIterator();
          return kit.hasNext() ? Optional.of(kit.next()) : Optional.empty();
        }
    }


    // Red-black mechanics

    private static final boolean RED   = false;
    private static final boolean BLACK = true;

    /**
     * Node in the Tree.  Doubles as a means to pass key-value pairs back to
     * user (see Map.Entry).
     */

    static final class Entry<K,V> implements Map.Entry<K,V> {
        K key;
        V value;
        Entry<K,V> left = null;
        Entry<K,V> right = null;
        Entry<K,V> parent;
        boolean color = BLACK;

        /**
         * Make a new cell with given key, value, and parent, and with
         * {@code null} child links, and BLACK color.
         */
        Entry(final K key, final V value, final Entry<K,V> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }

        /**
         * Returns the key.
         *
         * @return the key
         */
        @Override
        public K getKey() {
            return key;
        }

        /**
         * Returns the value associated with the key.
         *
         * @return the value associated with the key
         */
        @Override
        public V getValue() {
            return value;
        }

        /**
         * Replaces the value currently associated with the key with the given
         * value.
         *
         * @return the value associated with the key before this method was
         *         called
         */
        @Override
        public V setValue(final V value) {
            final V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            final Map.Entry<?,?> e = (Map.Entry<?,?>)o;

            return valEquals(key,e.getKey()) && valEquals(value,e.getValue());
        }

        @Override
        public int hashCode() {
            final int keyHash = (key==null ? 0 : key.hashCode());
            final int valueHash = (value==null ? 0 : value.hashCode());
            return keyHash ^ valueHash;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    /**
     * Returns the first Entry in the TreeMap (according to the TreeMap's
     * key-sort function).  Returns null if the TreeMap is empty.
     */
    final Entry<K,V> getFirstEntry() {
        Entry<K,V> p = root;
        while (p.left != null) p = p.left;
        return p;
    }

    /**
     * Returns the last Entry in the TreeMap (according to the TreeMap's
     * key-sort function).  Returns null if the TreeMap is empty.
     */
    final Entry<K,V> getLastEntry() {
        Entry<K,V> p = root;
        if (p != null)
            while (p.right != null)
                p = p.right;
        return p;
    }

    /**
     * Returns the successor of the specified Entry, or null if no such.
     */
    static <K,V> TreeMap.Entry<K,V> successor(final Entry<K,V> t) {
        assert t!=null;
        if (t.right != null) {
            Entry<K,V> p = t.right;
            while (p.left != null)
                p = p.left;
            return p;
        } else {
            Entry<K,V> p = t.parent;
            Entry<K,V> ch = t;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    /**
     * Returns the predecessor of the specified Entry, or null if no such.
     */
    static <K,V> Entry<K,V> predecessor(final Entry<K,V> t) {
        if (t == null)
            return null;
        else if (t.left != null) {
            Entry<K,V> p = t.left;
            while (p.right != null)
                p = p.right;
            return p;
        } else {
            Entry<K,V> p = t.parent;
            Entry<K,V> ch = t;
            while (p != null && ch == p.left) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }





    /**
     * Currently, we support Spliterator-based versions only for the
     * full map, in either plain of descending form, otherwise relying
     * on defaults because size estimation for submaps would dominate
     * costs. The type tests needed to check these for key views are
     * not very nice but avoid disrupting existing class
     * structures. Callers must use plain default spliterators if this
     * returns null.
     */
    static <K> Spliterator<K> keySpliteratorFor(final ISortedMap<K,?> m) {
        if (m instanceof TreeMap) {
            @SuppressWarnings("unchecked")
            final TreeMap<K,Object> t =
                (TreeMap<K,Object>) m;
            return t.keySpliterator();
        }
        if (m instanceof DescendingSubMap) {
            final DescendingSubMap<K,?> dm =
                (DescendingSubMap<K,?>) m;
            final TreeMap<K,?> tm = dm.m;
            if (dm == tm.descendingMap) {
                @SuppressWarnings("unchecked")
                final TreeMap<K,Object> t =
                    (TreeMap<K,Object>) tm;
                return t.descendingKeySpliterator();
            }
        }
        final NavigableSubMap<K,?> sm =
            (NavigableSubMap<K,?>) m;
        return sm.keySpliterator();
    }

    final Spliterator<K> keySpliterator() {
        return new KeySpliterator<>(this, null, null, 0, -1);
    }

    final Spliterator<K> descendingKeySpliterator() {
        return new DescendingKeySpliterator<>(this, null, null, 0, -2);
    }

    /**
     * Base class for spliterators.  Iteration starts at a given
     * origin and continues up to but not including a given fence (or
     * null for end).  At top-level, for ascending cases, the first
     * split uses the root as left-fence/right-origin. From there,
     * right-hand splits replace the current fence with its left
     * child, also serving as origin for the split-off spliterator.
     * Left-hands are symmetric. Descending versions place the origin
     * at the end and invert ascending split rules.  This base class
     * is non-commital about directionality, or whether the top-level
     * spliterator covers the whole tree. This means that the actual
     * split mechanics are located in subclasses. Some of the subclass
     * trySplit methods are identical (except for return types), but
     * not nicely factorable.
     *
     * Currently, subclass versions exist only for the full map
     * (including descending keys via its descendingMap).  Others are
     * possible but currently not worthwhile because submaps require
     * O(n) computations to determine size, which substantially limits
     * potential speed-ups of using custom Spliterators versus default
     * mechanics.
     *
     * To boostrap initialization, external constructors use
     * negative size estimates: -1 for ascend, -2 for descend.
     */
    static class TreeMapSpliterator<K,V> {
        final TreeMap<K,V> tree;
        TreeMap.Entry<K,V> current; // traverser; initially first node in range
        TreeMap.Entry<K,V> fence;   // one past last, or null
        int side;                   // 0: top, -1: is a left split, +1: right
        int est;                    // size estimate (exact only for top-level)

        TreeMapSpliterator(final TreeMap<K,V> tree,
                           final TreeMap.Entry<K,V> origin, final TreeMap.Entry<K,V> fence,
                           final int side, final int est) {
            this.tree = tree;
            this.current = origin;
            this.fence = fence;
            this.side = side;
            this.est = est;
        }

        final int getEstimate() { // force initialization
            int s; TreeMap<K,V> t;
            if ((s = est) < 0) {
                if ((t = tree) != null) {
                    current = (s == -1) ? t.getFirstEntry() : t.getLastEntry();
                    s = est = t.size;
                }
                else
                    s = est = 0;
            }
            return s;
        }

        public final long estimateSize() {
            return getEstimate();
        }
    }

    static final class KeySpliterator<K,V>
        extends TreeMapSpliterator<K,V>
        implements Spliterator<K> {
        KeySpliterator(final TreeMap<K,V> tree,
                       final TreeMap.Entry<K,V> origin, final TreeMap.Entry<K,V> fence,
                       final int side, final int est) {
            super(tree, origin, fence, side, est);
        }

        @Override
        public KeySpliterator<K,V> trySplit() {
            if (est < 0)
                getEstimate(); // force initialization
            final int d = side;
            final TreeMap.Entry<K,V> e = current, f = fence,
                s = ((e == null || e == f) ? null :      // empty
                     (d == 0)              ? tree.root : // was top
                     (d >  0)              ? e.right :   // was right
                     (d <  0 && f != null) ? f.left :    // was left
                     null);
            if (s != null && s != e && s != f &&
                tree.compare(e.key, s.key) < 0) {        // e not already past s
                side = 1;
                return new KeySpliterator<>
                    (tree, e, current = s, -1, est >>>= 1);
            }
            return null;
        }

        @Override
        public void forEachRemaining(final Consumer<? super K> action) {
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            final TreeMap.Entry<K,V> f = fence;
            TreeMap.Entry<K,V> e, p, pl;
            if ((e = current) != null && e != f) {
                current = f; // exhaust
                do {
                    action.accept(e.key);
                    if ((p = e.right) != null) {
                        while ((pl = p.left) != null)
                            p = pl;
                    }
                    else {
                        while ((p = e.parent) != null && e == p.right)
                            e = p;
                    }
                } while ((e = p) != null && e != f);
            }
        }

        @Override
        public boolean tryAdvance(final Consumer<? super K> action) {
            TreeMap.Entry<K,V> e;
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            if ((e = current) == null || e == fence)
                return false;
            current = successor(e);
            action.accept(e.key);
            return true;
        }

        @Override
        public int characteristics() {
            return (side == 0 ? Spliterator.SIZED : 0) |
                Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.ORDERED;
        }

        @Override
        public final Comparator<? super K>  getComparator() {
            return tree.comparator;
        }

    }

    static final class DescendingKeySpliterator<K,V>
        extends TreeMapSpliterator<K,V>
        implements Spliterator<K> {
        DescendingKeySpliterator(final TreeMap<K,V> tree,
                                 final TreeMap.Entry<K,V> origin, final TreeMap.Entry<K,V> fence,
                                 final int side, final int est) {
            super(tree, origin, fence, side, est);
        }

        @Override
        public DescendingKeySpliterator<K,V> trySplit() {
            if (est < 0)
                getEstimate(); // force initialization
            final int d = side;
            final TreeMap.Entry<K,V> e = current, f = fence,
                    s = ((e == null || e == f) ? null :      // empty
                         (d == 0)              ? tree.root : // was top
                         (d <  0)              ? e.left :    // was left
                         (d >  0 && f != null) ? f.right :   // was right
                         null);
            if (s != null && s != e && s != f &&
                tree.compare(e.key, s.key) > 0) {       // e not already past s
                side = 1;
                return new DescendingKeySpliterator<>
                        (tree, e, current = s, -1, est >>>= 1);
            }
            return null;
        }

        @Override
        public void forEachRemaining(final Consumer<? super K> action) {
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            final TreeMap.Entry<K,V> f = fence;
            TreeMap.Entry<K,V> e, p, pr;
            if ((e = current) != null && e != f) {
                current = f; // exhaust
                do {
                    action.accept(e.key);
                    if ((p = e.left) != null) {
                        while ((pr = p.right) != null)
                            p = pr;
                    }
                    else {
                        while ((p = e.parent) != null && e == p.left)
                            e = p;
                    }
                } while ((e = p) != null && e != f);
            }
        }

        @Override
        public boolean tryAdvance(final Consumer<? super K> action) {
            TreeMap.Entry<K,V> e;
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            if ((e = current) == null || e == fence)
                return false;
            current = predecessor(e);
            action.accept(e.key);
            return true;
        }

        @Override
        public int characteristics() {
            return (side == 0 ? Spliterator.SIZED : 0) |
                Spliterator.DISTINCT | Spliterator.ORDERED;
        }
    }

    static final class ValueSpliterator<K,V>
            extends TreeMapSpliterator<K,V>
            implements Spliterator<V> {
        ValueSpliterator(final TreeMap<K,V> tree,
                         final TreeMap.Entry<K,V> origin, final TreeMap.Entry<K,V> fence,
                         final int side, final int est) {
            super(tree, origin, fence, side, est);
        }

        @Override
        public ValueSpliterator<K,V> trySplit() {
            if (est < 0)
                getEstimate(); // force initialization
            final int d = side;
            final TreeMap.Entry<K,V> e = current, f = fence,
                    s = ((e == null || e == f) ? null :      // empty
                         (d == 0)              ? tree.root : // was top
                         (d >  0)              ? e.right :   // was right
                         (d <  0 && f != null) ? f.left :    // was left
                         null);
            if (s != null && s != e && s != f &&
                tree.compare(e.key, s.key) < 0) {        // e not already past s
                side = 1;
                return new ValueSpliterator<>
                        (tree, e, current = s, -1, est >>>= 1);
            }
            return null;
        }

        @Override
        public void forEachRemaining(final Consumer<? super V> action) {
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            final TreeMap.Entry<K,V> f = fence;
            TreeMap.Entry<K,V> e, p, pl;
            if ((e = current) != null && e != f) {
                current = f; // exhaust
                do {
                    action.accept(e.value);
                    if ((p = e.right) != null) {
                        while ((pl = p.left) != null)
                            p = pl;
                    }
                    else {
                        while ((p = e.parent) != null && e == p.right)
                            e = p;
                    }
                } while ((e = p) != null && e != f);
            }
        }

        @Override
        public boolean tryAdvance(final Consumer<? super V> action) {
            TreeMap.Entry<K,V> e;
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            if ((e = current) == null || e == fence)
                return false;
            current = successor(e);
            action.accept(e.value);
            return true;
        }

        @Override
        public int characteristics() {
            return (side == 0 ? Spliterator.SIZED : 0) | Spliterator.ORDERED;
        }
    }

    static final class EntrySpliterator<K,V>
        extends TreeMapSpliterator<K,V>
        implements Spliterator<Map.Entry<K,V>> {
        EntrySpliterator(final TreeMap<K,V> tree,
                         final TreeMap.Entry<K,V> origin, final TreeMap.Entry<K,V> fence,
                         final int side, final int est) {
            super(tree, origin, fence, side, est);
        }

        @Override
        public EntrySpliterator<K,V> trySplit() {
            if (est < 0)
                getEstimate(); // force initialization
            final int d = side;
            final TreeMap.Entry<K,V> e = current, f = fence,
                    s = ((e == null || e == f) ? null :      // empty
                         (d == 0)              ? tree.root : // was top
                         (d >  0)              ? e.right :   // was right
                         (d <  0 && f != null) ? f.left :    // was left
                         null);
            if (s != null && s != e && s != f &&
                tree.compare(e.key, s.key) < 0) {        // e not already past s
                side = 1;
                return new EntrySpliterator<>
                        (tree, e, current = s, -1, est >>>= 1);
            }
            return null;
        }

        @Override
        public void forEachRemaining(final Consumer<? super Map.Entry<K, V>> action) {
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            final TreeMap.Entry<K,V> f = fence;
            TreeMap.Entry<K,V> e, p, pl;
            if ((e = current) != null && e != f) {
                current = f; // exhaust
                do {
                    action.accept(e);
                    if ((p = e.right) != null) {
                        while ((pl = p.left) != null)
                            p = pl;
                    }
                    else {
                        while ((p = e.parent) != null && e == p.right)
                            e = p;
                    }
                } while ((e = p) != null && e != f);
            }
        }

        @Override
        public boolean tryAdvance(final Consumer<? super Map.Entry<K,V>> action) {
            TreeMap.Entry<K,V> e;
            if (action == null)
                throw new NullPointerException();
            if (est < 0)
                getEstimate(); // force initialization
            if ((e = current) == null || e == fence)
                return false;
            current = successor(e);
            action.accept(e);
            return true;
        }

        @Override
        public int characteristics() {
            return (side == 0 ? Spliterator.SIZED : 0) |
                    Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.ORDERED;
        }

        @Override
        public Comparator<Map.Entry<K, V>> getComparator() {
            // Adapt or create a key-based comparator
            if (tree.comparator != null) {
                return Map.Entry.comparingByKey(tree.comparator);
            }
            else {
                return (Comparator<Map.Entry<K, V>> & Serializable) (e1, e2) -> {
                    @SuppressWarnings("unchecked")
                    final
                    Comparable<? super K> k1 = (Comparable<? super K>) e1.getKey();
                    return k1.compareTo(e2.getKey());
                };
            }
        }
    }

}
