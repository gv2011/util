package com.github.gv2011.util.xml.imp;

import static com.github.gv2011.util.ex.Exceptions.call;

import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import com.github.gv2011.util.xml.XmlFactory;

public final class XmlFactoryImp implements XmlFactory {

  private final LSSerializer serializer;

  public XmlFactoryImp(){
    final LSSerializer serializer =
      ((DOMImplementationLS) call(DOMImplementationRegistry::newInstance).getDOMImplementation("LS"))
      .createLSSerializer()
    ;
    serializer.getDomConfig().setParameter("xml-declaration", false);
    this.serializer = serializer;
  }

  @Override
  public String toXmlString(final Element element) {
      return serializer.writeToString(element);
  }

}
