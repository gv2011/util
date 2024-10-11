package com.github.gv2011.util.html.imp;

import org.w3c.dom.Element;

import com.github.gv2011.util.html.BlockType;

class BlockBuilderImp extends AbstractBlockBuilder<BlockBuilderImp>{

  private final Element element;

  BlockBuilderImp(final AbstractBlockBuilder<?> parent, final BlockType blockType) {
    super(parent);
    element = parent.element().getOwnerDocument().createElement(blockType.toString());
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
