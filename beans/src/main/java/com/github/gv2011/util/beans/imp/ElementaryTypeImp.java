package com.github.gv2011.util.beans.imp;

import com.github.gv2011.util.beans.ElementaryTypeHandler;
import com.github.gv2011.util.json.JsonFactory;

final class ElementaryTypeImp<E> extends AbstractElementaryType<E>{

  private final ElementaryTypeHandler<E> handler;

  ElementaryTypeImp(final JsonFactory jf, final Class<E> clazz, final ElementaryTypeHandler<E> handler) {
    super(jf, clazz);
    this.handler = handler;
  }

  @Override
  ElementaryTypeHandler<E> handler() {
    return handler;
  }

}
