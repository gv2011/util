package com.github.gv2011.util.html.imp;

import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.net.URI;
import java.util.Optional;

import org.w3c.dom.Element;

import com.github.gv2011.util.html.BlockBuilder;
import com.github.gv2011.util.html.BlockType;
import com.github.gv2011.util.html.FormBuilder;

abstract class AbstractBlockBuilder<B extends AbstractBlockBuilder<B>> implements BlockBuilder{

  abstract B self();

  private final Optional<AbstractBlockBuilder<?>> parent;
  private final StringBuilder text = new StringBuilder();

  AbstractBlockBuilder(final Optional<AbstractBlockBuilder<?>> parent) {
    this.parent = parent;
  }

  @Override
  public final BlockBuilder close() {
    closeText();
    return parent.get();
  }

  abstract Element element();

  @Override
  public FormBuilder addForm() {
    closeText();
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
    this.text.append(text);
    return self();
  }

  @Override
  public final BlockBuilderImp addBlock() {
    closeText();
    return new BlockBuilderImp(this);
  }
  
  @Override
  public final BlockBuilder addAnchor(String text, URI url) {
    closeText();
    final Element anchor = element().getOwnerDocument().createElement("a");
    anchor.setAttribute("href", url.toASCIIString());
    anchor.setTextContent(text);
    element().appendChild(anchor);
    return this;
  }

  protected final void closeText(){
    if(text.length()>0){
      element().appendChild(element().getOwnerDocument().createTextNode(text.toString()));
      text.delete(0, text.length());
    }
  }

}
