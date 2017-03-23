/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
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

import static com.github.gv2011.util.Constants.newCachedConstant;

import java.util.Collection;
import java.util.Set;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.icol.IIterator;
import com.github.gv2011.util.icol.ISet;

/**
 * This class provides a skeletal implementation of the <tt>Set</tt>
 * interface to minimize the effort required to implement this
 * interface. <p>
 *
 * The process of implementing a set by extending this class is identical
 * to that of implementing a Collection by extending AbstractCollection,
 * except that all of the methods and constructors in subclasses of this
 * class must obey the additional constraints imposed by the <tt>Set</tt>
 * interface (for instance, the add method must not permit addition of
 * multiple instances of an object to a set).<p>
 *
 * Note that this class does not override any of the implementations from
 * the <tt>AbstractCollection</tt> class.  It merely adds implementations
 * for <tt>equals</tt> and <tt>hashCode</tt>.<p>
 *
 * This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @param <E> the type of elements maintained by this set
 *
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @see Collection
 * @see AbstractCollection
 * @see Set
 * @since 1.2
 */

public abstract class AbstractSet<E> extends AbstractCollection<E> implements ISet<E> {

  private final Constant<Integer> hashCode = newCachedConstant(this::calculateHashCode);


  protected AbstractSet() {}

    // Comparison and hashing

  @Override
  public boolean equals(final Object o) {
    if(o == this)return true;
    else if (!(o instanceof Set)) return false;
    else{
      final Collection<?> c = (Collection<?>) o;
      if (c.size() != size()) return false;
      else if(hashCode()!=o.hashCode()) return false;
      else return containsAll(c);
    }
  }

  @Override
  public int hashCode() {
    return hashCode.get();
  }

  private final int calculateHashCode() {
      int h = 0;
      final IIterator<E> i = iterator();
      while (i.hasNext()) {
          final E obj = i.next();
          if (obj != null)
              h += obj.hashCode();
      }
      return h;
  }

}
