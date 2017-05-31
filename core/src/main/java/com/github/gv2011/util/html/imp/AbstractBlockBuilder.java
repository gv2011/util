package com.github.gv2011.util.html.imp;

import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.util.Optional;

import org.w3c.dom.Element;

import com.github.gv2011.util.html.BlockBuilder;
import com.github.gv2011.util.html.BlockType;
import com.github.gv2011.util.html.FormBuilder;

abstract class AbstractBlockBuilder<B extends AbstractBlockBuilder<B>> implements BlockBuilder{

  abstract B self();

  private final Optional<AbstractBlockBuilder<?>> parent;

  AbstractBlockBuilder(final Optional<AbstractBlockBuilder<?>> parent) {
    this.parent = parent;
  }

  @Override
  public final BlockBuilder close() {
    return parent.get();
  }

  abstract Element element();

  @Override
  public FormBuilder addForm() {
    return new FormBuilderImp(this);
  }

  @Override
  public final BlockType blockType(final String name) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public final B setBlockType(final BlockType blockType) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public final B addText(final String text) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public final BlockBuilderImp addBlock() {
    return new BlockBuilderImp(this);
  }

}
