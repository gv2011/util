package com.github.gv2011.util.icol;

import java.util.Map.Entry;

public interface IEntry<K,V> extends Entry<K,V>{

  @Override
  default V setValue(final V value) {
    throw new UnsupportedOperationException();
  }

}
