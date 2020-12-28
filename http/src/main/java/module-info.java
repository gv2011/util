module com.github.gv2011.http{
  requires com.github.gv2011.util;
  requires org.apache.httpcomponents.httpcore;
  requires org.apache.httpcomponents.httpclient;
  requires org.eclipse.jetty.server;
  
  provides com.github.gv2011.util.http.HttpFactory with com.github.gv2011.http.imp.HttpFactoryImp;
}