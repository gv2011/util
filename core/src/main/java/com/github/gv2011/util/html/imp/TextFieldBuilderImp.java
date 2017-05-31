package com.github.gv2011.util.html.imp;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.github.gv2011.util.html.FormBuilder;
import com.github.gv2011.util.html.TextFieldBuilder;

class TextFieldBuilderImp implements TextFieldBuilder {

  private final FormBuilderImp parent;
  private String name;
  private boolean password;
  private final Element div;
  private final Element input;

  TextFieldBuilderImp(final FormBuilderImp formBuilder) {
    parent = formBuilder;
    final Element form = parent.element();
    final Document doc = form.getOwnerDocument();
    div = doc.createElement("div");
    input = doc.createElement("input");
    div.appendChild(input);
    form.appendChild(div);
    form.appendChild(doc.createTextNode("\n"));
  }

  @Override
  public FormBuilder close() {
    input.setAttribute("name", name);
    input.setAttribute("id", name);
    input.setAttribute("type", password?"password":"text");
    return parent;
  }

  @Override
  public TextFieldBuilder setName(final String name) {
    this.name = name;
    return this;
  }

  @Override
  public TextFieldBuilder setPassword() {
    password = true;
    return this;
  }

}
