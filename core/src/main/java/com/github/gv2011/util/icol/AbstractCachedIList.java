package com.github.gv2011.util.icol;

/*-
 * %---license-start---
 * The MIT License (MIT)
 * %
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * %---license-end---
 */



import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;

public abstract class AbstractCachedIList<E> extends AbstractIList<E>{

  protected final Constant<HashMap<E,int[]>> indexCache = Constants.softRefConstant(this::createIndex);
  protected final Constant<ISortedMap<Integer,E>> asMapCache = Constants.softRefConstant(super::asMap);

  private final HashMap<E,int[]> createIndex(){
    final HashMap<E,int[]> result = new HashMap<>();
    for(int i=0; i<size(); i++){
      final E e = get(i);
      int[] indices = result.get(e);
      if(indices==null){
        result.put(e, new int[]{i});
      }else{
        indices = Arrays.copyOf(indices, indices.length+1);
        indices[indices.length-1] = i;
        result.put(e, new int[]{i});
      }
    }
    return result;
  }

  @Override
  public boolean contains(final Object o) {
    if(isEmpty()) return false;
    else return indexCache.get().containsKey(o);
  }

  @Override
  public boolean containsAll(final Collection<?> c) {
    if(this==c) return true;
    else if(isEmpty()) return c.isEmpty();
    else return indexCache.get().keySet().containsAll(c);
  }


  @Override
  public int indexOf(final Object o) {
    final int[] indices = indexCache.get().get(o);
    return indices==null ? -1 : indices[0];
  }

  @Override
  public int lastIndexOf(final Object o) {
    final int[] indices = indexCache.get().get(o);
    return indices==null ? -1 : indices[indices.length-1];
  }

  @Override
  public ISortedMap<Integer, E> asMap() {
    return asMapCache.get();
  }

}
