package com.github.gv2011.util.html.imp;

import java.util.Optional;

import org.w3c.dom.Element;

class BlockBuilderImp extends AbstractBlockBuilder<BlockBuilderImp>{

  private final Element element;

  BlockBuilderImp(final AbstractBlockBuilder<?> parent) {
    super(Optional.of(parent));
    element = parent.element().getOwnerDocument().createElement("div");
    parent.element().appendChild(element);
  }

  @Override
  BlockBuilderImp self() {
    return this;
  }

  @Override
  Element element() {
    return element;
  }

}
