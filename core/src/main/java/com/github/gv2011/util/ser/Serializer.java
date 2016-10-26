package com.github.gv2011.util.ser;

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
