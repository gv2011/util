package com.github.gv2011.util.html.imp;

import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import com.github.gv2011.util.html.HtmlBuilder;
import com.github.gv2011.util.html.HtmlDocument;
import com.github.gv2011.util.html.HtmlFactory;
import com.github.gv2011.util.xml.DomUtils;

public class HtmlBuilderImp extends AbstractBlockBuilder<HtmlBuilderImp> implements HtmlBuilder{

  private final Document doc;
  private final Element head;
  private final Element body;
  private final Element html;

  public HtmlBuilderImp(final HtmlFactory factory) {
    super(factory);
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

//    final Element style = doc.createElement("style");
//    style.appendChild(doc.createTextNode(
//      "table {border-collapse:collapse;}\n"+
//      "td {border:1px solid black;}\n"
//    ));
//    head.appendChild(style);
//
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
    closeText();
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

  @Override
  public HtmlBuilder setStyle(final String style) {
    final Element styleElement = doc.createElement("style");
    styleElement.appendChild(doc.createTextNode(style));
    DomUtils.setChild(head, styleElement);
    return this;
  }

}
