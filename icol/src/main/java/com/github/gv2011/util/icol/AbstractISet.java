package com.github.gv2011.util.icol;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;

abstract class AbstractISet<S extends Set<I>, I, O> extends AbstractSet<O> implements ISet<O>{

  private final Constant<Integer> hash;

  AbstractISet() {
    hash = Constants.newCachedConstant(super::hashCode);
  }

  protected abstract S delegate();

  protected abstract Function<I,O> mapping();

  @Override
  public final Iterator<O> iterator() {
    return new AbstractIterator<>(delegate().iterator(), mapping());
  }

  @Override
  public final int size() {
    return delegate().size();
  }

  @Override
  public final boolean isEmpty() {
    return false;
  }

  @Override
  public abstract boolean contains(final Object o);

  @Override
  public boolean containsAll(final Collection<?> c){
    if(c instanceof Set ? c.size()>size() : false){
      return false;
    }
    else return super.containsAll(c);
  }

  @Override
  public final boolean equals(final Object o) {
    if(o==this) return true;
    else if(!(o instanceof Set)) return false;
    else{
      final Set<?> other = (Set<?>)o;
      if(size()!=other.size()) return false;
      else{
        if(other instanceof AbstractISet){
          if(hashCode()!=other.hashCode()) return false;
          else return containsAll(other);
        }
        else return containsAll(other);
      }
    }
  }

  @Override
  public final int hashCode() {
    return hash.get();
  }

}
