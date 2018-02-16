package com.github.gv2011.util.ser;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %%
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
 * #L%
 */




import java.util.Map;
import java.util.Map.Entry;

import com.github.gv2011.util.ann.Nullable;

public class Serializer<E,A,O> {

  private final ElementarySerializer<E,A,O> eSer;
  private final TypeSupport<A,E> ts;

  public Serializer(final ElementarySerializer<E,A,O> eSer, final TypeSupport<A,E> typeSupport) {
    this.eSer = eSer;
    this.ts = typeSupport;
  }

  public void serialize(final @Nullable Object obj){
    eSer.startDocument();
    serializeFragment(obj);
    eSer.endDocument();
  }

  public void serializeFragment(final @Nullable Object obj){
    if(obj==null) eSer.serializeNull();
    else if(ts.isElementary(obj)) eSer.serializeElementary(ts.asElementary(obj));
    else if(ts.isBean(obj)) serializeBean(ts.asBean(obj));
    else if(ts.isMap(obj)) serializeMap(ts.asMap(obj));
    else if(ts.isList(obj)) serializeList(ts.asList(obj));
    else throw new IllegalArgumentException();
  }

  private void serializeBean(final Map<A, ?> map) {
    eSer.startBean();
    for(final Entry<A,?> e: map.entrySet()){
      serializeFragment(e.getKey());
      serializeFragment(e.getValue());
    }
    eSer.endBean();
  }



  private void serializeList(final Iterable<?> list) {
    eSer.startList();
    for(@Nullable final Object e: list){
      serializeFragment(e);
    }
    eSer.endList();
  }

  private void serializeMap(final Map<?, ?> map) {
    eSer.startMap();
    for(final Entry<?,?> e: map.entrySet()){
      serializeFragment(e.getKey());
      serializeFragment(e.getValue());
    }
    eSer.endMap();
  }



}
