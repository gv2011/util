module com.github.gv2011.util.html.imp{

  requires transitive com.github.gv2011.util;
  requires xercesImpl;
  requires java.xml;

  provides com.github.gv2011.util.html.HtmlFactory with com.github.gv2011.util.html.imp.HtmlFactoryImp;
  provides com.github.gv2011.util.xml.XmlFactory with com.github.gv2011.util.xml.imp.XmlFactoryImp;
}