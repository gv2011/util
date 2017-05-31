package com.github.gv2011.util.html;

import java.io.OutputStream;

import org.w3c.dom.Document;

public interface HtmlDocument {

  Document dom();

  String title();

  long write(OutputStream out);

}
