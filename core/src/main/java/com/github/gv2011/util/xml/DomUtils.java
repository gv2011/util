package com.github.gv2011.util.xml;

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



import static com.github.gv2011.util.CollectionUtils.toOptional;
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.run;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.util.AbstractList;
import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public final class DomUtils {

  private DomUtils(){staticClass();}

  public static Document newDocument(){
    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    dbf.setValidating(true);
    final DocumentBuilder docBuilder = call(()->dbf.newDocumentBuilder());
    final DOMImplementation domImpl = docBuilder.getDOMImplementation();
    final DocumentType docType = domImpl.createDocumentType(
      "html", "-//W3C//DTD XHTML 1.0 Strict//EN", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
    final Document document = domImpl.createDocument("http://www.w3.org/1999/xhtml", "html", docType);
    verifyEqual(document.getDoctype(), docType);
    return document;
//    return docBuilder.newDocument();
  }

  public static String toString(final Document doc){
    final DOMImplementationLS dom =
      (DOMImplementationLS) call(()->DOMImplementationRegistry.newInstance()).getDOMImplementation("LS")
    ;
    final LSSerializer serializer = dom.createLSSerializer();
    serializer.setNewLine("\n");
    final LSOutput destination = dom.createLSOutput();
    destination.setEncoding(UTF_8.name());
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    destination.setByteStream(bos);
    serializer.write(doc, destination);
    return new String(bos.toByteArray(), UTF_8);
  }

  public static String toString1(final Document doc){
    final DocumentType doctype = notNull(doc.getDoctype());
    final DOMSource domSource = new DOMSource(doc);
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    final StreamResult result = new StreamResult(bos);
    final TransformerFactory tf = TransformerFactory.newInstance();
    final Transformer transformer = call(()->tf.newTransformer());
    transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
    transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
    transformer.setOutputProperty(OutputKeys.ENCODING, UTF_8.name());
    run(()->transformer.transform(domSource, result));
    return new String(bos.toByteArray(), UTF_8);
  }

  public static void setChild(final Element e, final Element child) {
    final String tag = child.getTagName();
    final Optional<Element> previous = getChild(e, tag);
    if(previous.isPresent()){
      e.replaceChild(child, previous.get());
    }
    else e.appendChild(child);
  }

  public static Optional<Element> getChild(final Element e, final String tag) {
    return stream(e.getChildNodes())
      .filter(n->n.getNodeType()==Node.ELEMENT_NODE)
      .map(n->(Element)n)
      .filter(ce->ce.getTagName().equals(tag))
      .collect(toOptional())
    ;
  }

  public static Stream<Node> stream(final NodeList nodeList) {
    return new NodeListWrapper(nodeList).stream();
  }

  private static final class NodeListWrapper extends AbstractList<Node>{
    private final NodeList nodes;
    private NodeListWrapper(final NodeList nodes) {
      this.nodes = nodes;
    }
    @Override
    public Node get(final int index) {
      return notNull(nodes.item(index));
    }

    @Override
    public int size() {
      return nodes.getLength();
    }}
}
