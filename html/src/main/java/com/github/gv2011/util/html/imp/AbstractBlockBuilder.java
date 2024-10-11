package com.github.gv2011.util.html.imp;

import java.net.URI;
import java.util.Optional;

import org.w3c.dom.Element;

import com.github.gv2011.util.html.BlockBuilder;
import com.github.gv2011.util.html.BlockType;
import com.github.gv2011.util.html.FormBuilder;
import com.github.gv2011.util.html.HtmlFactory;

abstract class AbstractBlockBuilder<B extends AbstractBlockBuilder<B>> implements BlockBuilder{

  abstract B self();

  private final HtmlFactory factory;
  private final Optional<AbstractBlockBuilder<?>> parent;
  private final StringBuilder text = new StringBuilder();

  AbstractBlockBuilder(final AbstractBlockBuilder<?> parent) {
    factory = parent.factory;
    this.parent = Optional.of(parent);
  }

  AbstractBlockBuilder(final HtmlFactory factory) {
    this.factory = factory;
    this.parent = Optional.empty();
  }

  @Override
  public final HtmlFactory factory() {
    return factory;
  }

  @Override
  public final BlockBuilder close() {
    closeText();
    return parent.get();
  }

  abstract Element element();

  @Override
  public final FormBuilder addForm() {
    closeText();
    return new FormBuilderImp(this);
  }

  @Override
  public final B addText(final String text) {
    this.text.append(text);
    return self();
  }

  @Override
  public final BlockBuilder addBlock() {
    return addBlock(BlockTypeImp.div);
  }

  @Override
  public final BlockBuilder addBlock(final BlockType blockType) {
    closeText();
    return new BlockBuilderImp(this, blockType);
  }

  @Override
  public final BlockBuilder addAnchor(final String text, final URI url) {
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
