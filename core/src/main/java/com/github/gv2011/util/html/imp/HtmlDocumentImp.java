package com.github.gv2011.util.html.imp;

import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;

import java.io.OutputStream;

import org.w3c.dom.Document;

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
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public long write(final OutputStream out) {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

}
