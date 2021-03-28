module com.github.gv2011.jacksonadapter{
  requires transitive com.fasterxml.jackson.databind;
  requires transitive com.github.gv2011.util.json.imp;
  
  requires org.slf4j;
  requires com.fasterxml.jackson.core;
  requires com.github.gv2011.util.beans.imp;
  requires com.github.gv2011.util;
  
  provides com.fasterxml.jackson.databind.Module with com.github.gv2011.jacksonadapter.ImmutableBeansModule;
  provides com.github.gv2011.util.json.Adapter with com.github.gv2011.jacksonadapter.JacksonAdapter;
  
  /** For tests only **/
  exports com.github.gv2011.jacksonadapter to com.github.gv2011.util.beans.imp;

}