package com.github.gv2011.util.html.imp;

import java.util.Optional;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.github.gv2011.util.html.FormBuilder;
import com.github.gv2011.util.html.TextFieldBuilder;

class FormBuilderImp extends AbstractBlockBuilder<FormBuilderImp> implements FormBuilder {

  private final Element form;

  FormBuilderImp(final AbstractBlockBuilder<?> parent) {
    super(Optional.of(parent));
    final Document doc = parent.element().getOwnerDocument();
    form = doc.createElement("form");
    form.setAttribute("action", "#");
    form.setAttribute("method", "POST");
    form.setAttribute("enctype", "multipart/form-data");
    parent.element().appendChild(form);
    form.appendChild(doc.createTextNode("\n"));
  }

  @Override
  FormBuilderImp self() {
    return this;
  }

  @Override
  public TextFieldBuilder addTextField() {
    return new TextFieldBuilderImp(this);
  }

  @Override
  public FormBuilder addSubmit() {
    final Document doc = form.getOwnerDocument();
    final Element div = doc.createElement("div");
    final Element button = doc.createElement("button");
    button.setAttribute("id", "submit");
    final Text buttonTxt = doc.createTextNode("Login");
    button.appendChild(buttonTxt);
    div.appendChild(button);
    form.appendChild(div);
    form.appendChild(doc.createTextNode("\n"));
    return this;
  }

  @Override
  Element element() {
    return form;
  }


}
