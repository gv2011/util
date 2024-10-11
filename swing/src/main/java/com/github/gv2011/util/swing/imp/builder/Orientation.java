package com.github.gv2011.util.swing.imp.builder;

import java.awt.Dimension;
import java.util.function.ToIntFunction;

enum Orientation {
  HORIZONTAL(d->d.width), VERTICAL(d->d.height);

  private final ToIntFunction<Dimension> f;

  private Orientation(final ToIntFunction<Dimension> f){
    this.f = f;
  }

  int size(final Dimension dimension){
    return f.applyAsInt(dimension);
  }

  Orientation other() {
    return this==HORIZONTAL ? VERTICAL : HORIZONTAL;
  }
}
