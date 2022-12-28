module com.github.gv2011.http{
  requires transitive com.github.gv2011.util;
  requires org.apache.httpcomponents.core5.httpcore5;
  requires org.apache.httpcomponents.client5.httpclient5;
  requires org.eclipse.jetty.server;
  requires org.eclipse.jetty.http;
  requires org.eclipse.jetty.util;
  requires com.ibm.icu;
  requires org.shredzone.acme4j;
  requires org.shredzone.acme4j.utils;
  
  uses com.github.gv2011.util.html.HtmlFactory;
  
  exports com.github.gv2011.http.imp to com.github.gv2011.util;
  
  provides com.github.gv2011.util.http.HttpFactory with com.github.gv2011.http.imp.HttpFactoryImp;
  provides com.github.gv2011.util.uc.UnicodeProvider with com.github.gv2011.http.imp.IcuUnicodeProvider;
}