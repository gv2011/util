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




import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;

import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import com.github.gv2011.util.html.HtmlBuilder;
import com.github.gv2011.util.html.HtmlDocument;
import com.github.gv2011.util.xml.DomUtils;

public class HtmlBuilderImp extends AbstractBlockBuilder<HtmlBuilderImp> implements HtmlBuilder{

  private final Document doc;
  private final Element head;
  private final Element body;
  private final Element html;

  public HtmlBuilderImp() {
    super(Optional.empty());
    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    dbf.setValidating(true);
    final DocumentBuilder docBuilder = call(()->dbf.newDocumentBuilder());
    final DOMImplementation domImpl = docBuilder.getDOMImplementation();
    final DocumentType docType = domImpl.createDocumentType(
      "html", "-//W3C//DTD XHTML 1.0 Strict//EN", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
    doc = domImpl.createDocument("http://www.w3.org/1999/xhtml", "html", docType);
    verifyEqual(doc.getDoctype(), docType);
    html = notNull(doc.getDocumentElement());
    head = doc.createElement("head");
    body = doc.createElement("body");
    html.appendChild(doc.createTextNode("\n"));
    html.appendChild(head);
    html.appendChild(doc.createTextNode("\n"));
    html.appendChild(body);
    html.appendChild(doc.createTextNode("\n"));
  }



  @Override
  HtmlBuilderImp self() {
    return this;
  }



  @Override
  Element element() {
    return body;
  }



  @Override
  public HtmlDocument build() {
    doc.getDocumentElement().normalize();
    return new HtmlDocumentImp(doc);
  }

  @Override
  public HtmlBuilder setTitle(final String title) {
    final Element titleElement = doc.createElement("title");
    titleElement.appendChild(doc.createTextNode(title));
    DomUtils.setChild(head, titleElement);
    return this;
  }

}
