/*
 * Copyright (c) 1997, 2012, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.github.gv2011.util.icol2;

import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import com.github.gv2011.util.icol.IIterator;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IListIterator;
import com.github.gv2011.util.icol.ISortedMap;

/**
 * This class provides a skeletal implementation of the {@link List}
 * interface to minimize the effort required to implement this interface
 * backed by a "random access" data store (such as an array).  For sequential
 * access data (such as a linked list), {@link AbstractSequentialList} should
 * be used in preference to this class.
 *
 * <p>To implement an unmodifiable list, the programmer needs only to extend
 * this class and provide implementations for the {@link #get(int)} and
 * {@link List#size() size()} methods.
 *
 * <p>To implement a modifiable list, the programmer must additionally
 * override the {@link #set(int, Object) set(int, E)} method (which otherwise
 * throws an {@code UnsupportedOperationException}).  If the list is
 * variable-size the programmer must additionally override the
 * {@link #add(int, Object) add(int, E)} and {@link #remove(int)} methods.
 *
 * <p>The programmer should generally provide a void (no argument) and collection
 * constructor, as per the recommendation in the {@link Collection} interface
 * specification.
 *
 * <p>Unlike the other abstract collection implementations, the programmer does
 * <i>not</i> have to provide an iterator implementation; the iterator and
 * list iterator are implemented by this class, on top of the "random access"
 * methods:
 * {@link #get(int)},
 * {@link #set(int, Object) set(int, E)},
 * {@link #add(int, Object) add(int, E)} and
 * {@link #remove(int)}.
 *
 * <p>The documentation for each non-abstract method in this class describes its
 * implementation in detail.  Each of these methods may be overridden if the
 * collection being implemented admits a more efficient implementation.
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @since 1.2
 */

public abstract class AbstractList<E> extends AbstractCollection<E> implements IList<E> {
    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
     */
    protected AbstractList() {
    }


     /**
     * {@inheritDoc}
     *
     * <p>This implementation first gets a list iterator (with
     * {@code listIterator()}).  Then, it iterates over the list until the
     * specified element is found or the end of the list is reached.
     *
     * @throws ClassCastException   {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public int indexOf(final Object o) {
        final IListIterator<E> it = listIterator();
        if (o==null) {
            while (it.hasNext())
                if (it.next()==null)
                    return it.previousIndex();
        } else {
            while (it.hasNext())
                if (o.equals(it.next()))
                    return it.previousIndex();
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation first gets a list iterator that points to the end
     * of the list (with {@code listIterator(size())}).  Then, it iterates
     * backwards over the list until the specified element is found, or the
     * beginning of the list is reached.
     *
     * @throws ClassCastException   {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public int lastIndexOf(final Object o) {
        final IListIterator<E> it = listIterator(size());
        if (o==null) {
            while (it.hasPrevious())
                if (it.previous()==null)
                    return it.nextIndex();
        } else {
            while (it.hasPrevious())
                if (o.equals(it.previous()))
                    return it.nextIndex();
        }
        return -1;
    }



    // Iterators

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     *
     * <p>This implementation returns a straightforward implementation of the
     * iterator interface, relying on the backing list's {@code size()},
     * {@code get(int)}, and {@code remove(int)} methods.
     *
     * <p>Note that the iterator returned by this method will throw an
     * {@link UnsupportedOperationException} in response to its
     * {@code remove} method unless the list's {@code remove(int)} method is
     * overridden.
     *
     * <p>This implementation can be made to throw runtime exceptions in the
     * face of concurrent modification, as described in the specification
     * for the (protected) {@link #modCount} field.
     *
     * @return an iterator over the elements in this list in proper sequence
     */
    @Override
    public IIterator<E> iterator() {
        return new Itr();
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns {@code listIterator(0)}.
     *
     * @see #listIterator(int)
     */
    @Override
    public IListIterator<E> listIterator() {
        return listIterator(0);
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns a straightforward implementation of the
     * {@code ListIterator} interface that extends the implementation of the
     * {@code Iterator} interface returned by the {@code iterator()} method.
     * The {@code ListIterator} implementation relies on the backing list's
     * {@code get(int)}, {@code set(int, E)}, {@code add(int, E)}
     * and {@code remove(int)} methods.
     *
     * <p>Note that the list iterator returned by this implementation will
     * throw an {@link UnsupportedOperationException} in response to its
     * {@code remove}, {@code set} and {@code add} methods unless the
     * list's {@code remove(int)}, {@code set(int, E)}, and
     * {@code add(int, E)} methods are overridden.
     *
     * <p>This implementation can be made to throw runtime exceptions in the
     * face of concurrent modification, as described in the specification for
     * the (protected) {@link #modCount} field.
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public IListIterator<E> listIterator(final int index) {
        return new ListItr(index);
    }

    private class Itr implements IIterator<E> {
        /**
         * Index of element to be returned by subsequent call to next.
         */
        int cursor = 0;

        @Override
        public boolean hasNext() {
            return cursor != size();
        }

        @Override
        public E next() {
            try {
                final int i = cursor;
                final E next = get(i);
                cursor = i + 1;
                return next;
            } catch (final IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }
    }

    private class ListItr extends Itr implements IListIterator<E> {
        ListItr(final int index) {
            cursor = index;
        }

        @Override
        public boolean hasPrevious() {
            return cursor != 0;
        }

        @Override
        public E previous() {
            try {
                final int i = cursor - 1;
                final E previous = get(i);
                cursor = i;
                return previous;
            } catch (final IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor-1;
        }

    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns a list that subclasses
     * {@code AbstractList}.  The subclass stores, in private fields, the
     * offset of the subList within the backing list, the size of the subList
     * (which can change over its lifetime), and the expected
     * {@code modCount} value of the backing list.  There are two variants
     * of the subclass, one of which implements {@code RandomAccess}.
     * If this list implements {@code RandomAccess} the returned list will
     * be an instance of the subclass that implements {@code RandomAccess}.
     *
     * <p>The subclass's {@code set(int, E)}, {@code get(int)},
     * {@code add(int, E)}, {@code remove(int)}, {@code addAll(int,
     * Collection)} and {@code removeRange(int, int)} methods all
     * delegate to the corresponding methods on the backing abstract list,
     * after bounds-checking the index and adjusting for the offset.  The
     * {@code addAll(Collection c)} method merely returns {@code addAll(size,
     * c)}.
     *
     * <p>The {@code listIterator(int)} method returns a "wrapper object"
     * over a list iterator on the backing list, which is created with the
     * corresponding method on the backing list.  The {@code iterator} method
     * merely returns {@code listIterator()}, and the {@code size} method
     * merely returns the subclass's {@code size} field.
     *
     * <p>All methods first check to see if the actual {@code modCount} of
     * the backing list is equal to its expected value, and throw a
     * {@code ConcurrentModificationException} if it is not.
     *
     * @throws IndexOutOfBoundsException if an endpoint index value is out of range
     *         {@code (fromIndex < 0 || toIndex > size)}
     * @throws IllegalArgumentException if the endpoint indices are out of order
     *         {@code (fromIndex > toIndex)}
     */
    @Override
    public IList<E> subList(final int fromIndex, final int toIndex) {
        return (this instanceof RandomAccess ?
                new RandomAccessSubList<>(this, fromIndex, toIndex) :
                new SubList<>(this, fromIndex, toIndex));
    }

    @Override
    public final ISortedMap<Integer, E> asMap() {
      throw notYetImplementedException();
    }



    // Comparison and hashing

    /**
     * Compares the specified object with this list for equality.  Returns
     * {@code true} if and only if the specified object is also a list, both
     * lists have the same size, and all corresponding pairs of elements in
     * the two lists are <i>equal</i>.  (Two elements {@code e1} and
     * {@code e2} are <i>equal</i> if {@code (e1==null ? e2==null :
     * e1.equals(e2))}.)  In other words, two lists are defined to be
     * equal if they contain the same elements in the same order.<p>
     *
     * This implementation first checks if the specified object is this
     * list. If so, it returns {@code true}; if not, it checks if the
     * specified object is a list. If not, it returns {@code false}; if so,
     * it iterates over both lists, comparing corresponding pairs of elements.
     * If any comparison returns {@code false}, this method returns
     * {@code false}.  If either iterator runs out of elements before the
     * other it returns {@code false} (as the lists are of unequal length);
     * otherwise it returns {@code true} when the iterations complete.
     *
     * @param o the object to be compared for equality with this list
     * @return {@code true} if the specified object is equal to this list
     */
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof List))
            return false;

        final IListIterator<E> e1 = listIterator();
        final ListIterator<?> e2 = ((List<?>) o).listIterator();
        while (e1.hasNext() && e2.hasNext()) {
            final E o1 = e1.next();
            final Object o2 = e2.next();
            if (!(o1==null ? o2==null : o1.equals(o2)))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }

    /**
     * Returns the hash code value for this list.
     *
     * <p>This implementation uses exactly the code that is used to define the
     * list hash function in the documentation for the {@link List#hashCode}
     * method.
     *
     * @return the hash code value for this list
     */
    @Override
    public int hashCode() {
        int hashCode = 1;
        for (final E e : this)
            hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
        return hashCode;
    }
}

class SubList<E> extends AbstractList<E> {
    private final AbstractList<E> l;
    private final int offset;
    private final int size;

    SubList(final AbstractList<E> list, final int fromIndex, final int toIndex) {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > list.size())
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex +
                                               ") > toIndex(" + toIndex + ")");
        l = list;
        offset = fromIndex;
        size = toIndex - fromIndex;
    }

    @Override
    public E get(final int index) {
        rangeCheck(index);
        return l.get(index+offset);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public IIterator<E> iterator() {
        return listIterator();
    }

    @Override
    public IListIterator<E> listIterator(final int index) {

        return new IListIterator<E>() {
            private final IListIterator<E> i = l.listIterator(index+offset);

            @Override
            public boolean hasNext() {
                return nextIndex() < size;
            }

            @Override
            public E next() {
                if (hasNext())
                    return i.next();
                else
                    throw new NoSuchElementException();
            }

            @Override
            public boolean hasPrevious() {
                return previousIndex() >= 0;
            }

            @Override
            public E previous() {
                if (hasPrevious())
                    return i.previous();
                else
                    throw new NoSuchElementException();
            }

            @Override
            public int nextIndex() {
                return i.nextIndex() - offset;
            }

            @Override
            public int previousIndex() {
                return i.previousIndex() - offset;
            }
        };
    }

    @Override
    public IList<E> subList(final int fromIndex, final int toIndex) {
        return new SubList<>(this, fromIndex, toIndex);
    }

    private void rangeCheck(final int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private String outOfBoundsMsg(final int index) {
        return "Index: "+index+", Size: "+size;
    }

}

class RandomAccessSubList<E> extends SubList<E> implements RandomAccess {
    RandomAccessSubList(final AbstractList<E> list, final int fromIndex, final int toIndex) {
        super(list, fromIndex, toIndex);
    }

    @Override
    public IList<E> subList(final int fromIndex, final int toIndex) {
        return new RandomAccessSubList<>(this, fromIndex, toIndex);
    }
}
