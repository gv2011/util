module com.github.gv2011.jacksonadapter{
  requires org.slf4j;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires com.github.gv2011.util.beans.imp;
  requires com.github.gv2011.util.json.imp;
  
  provides com.fasterxml.jackson.databind.Module with com.github.gv2011.jacksonadapter.ImmutableBeansModule;
  provides com.github.gv2011.util.json.imp.Adapter with com.github.gv2011.jacksonadapter.JacksonAdapter;
  
}