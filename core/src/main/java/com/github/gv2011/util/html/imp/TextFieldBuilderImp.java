package com.github.gv2011.util.html.imp;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %%
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
 * #L%
 */




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
