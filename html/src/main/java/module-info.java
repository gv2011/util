module com.github.gv2011.util.html.imp{
  
  requires transitive com.github.gv2011.util;
 
  provides com.github.gv2011.util.html.HtmlFactory with com.github.gv2011.util.html.imp.HtmlFactoryImp;
}