package com.github.gv2011.util.html.imp;

import static com.github.gv2011.util.xml.DomUtils.getChild;

import java.io.OutputStream;

import org.w3c.dom.Document;

import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.html.HtmlDocument;
import com.github.gv2011.util.xml.DomUtils;

class HtmlDocumentImp implements HtmlDocument {

  private final Document dom;

  HtmlDocumentImp(final Document dom) {
    this.dom = dom;
  }

  @Override
  public Document dom() {
    return dom;
  }

  @Override
  public String toString() {
    return DomUtils.toString(dom);
  }
  
  @Override
  public String title() {
    return getChild(getChild(dom.getDocumentElement(), "head").get(), "title").get().getTextContent();
  }

  @Override
  public long write(final OutputStream out) {
    return DomUtils.write(dom, out);
  }

  @Override
  public TypedBytes asEntity() {
    return DomUtils.toTypedBytes(dom);
  }

}
