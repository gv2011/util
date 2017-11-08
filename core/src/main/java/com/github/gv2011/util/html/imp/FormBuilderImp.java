package com.github.gv2011.util.html.imp;

/*-
 * %---license-start---
 * The MIT License (MIT)
 * %
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * %---license-end---
 */



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
