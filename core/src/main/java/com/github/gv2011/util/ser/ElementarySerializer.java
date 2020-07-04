package com.github.gv2011.util.ser;


import java.io.Flushable;

@SuppressWarnings("unused")
public interface ElementarySerializer<E,A,O> extends Flushable{

  void startDocument();
  void endDocument();

  void startList();
  void endList();

  void startBean();
  void endBean();

  void startBeanEntry();
  void startBeanValue();
  void endBeanEntry();

  void startMap();
  void endMap();

  void startMapEntry();
  void startMapValue();
  void endMapEntry();

  void serializeElementary(E elementary);
  void serializeNull();

  @Override
  void flush();

}
