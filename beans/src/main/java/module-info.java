module com.github.gv2011.util.beans.imp{
  requires org.slf4j;
  requires transitive com.github.gv2011.util;

  uses com.github.gv2011.util.json.JsonFactory;
  uses com.github.gv2011.util.beans.ElementaryTypeHandlerFactory;

  exports com.github.gv2011.util.beans.imp;

  provides com.github.gv2011.util.beans.TypeRegistry with com.github.gv2011.util.beans.imp.DefaultTypeRegistry;
}
