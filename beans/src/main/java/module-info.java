module com.github.gv2011.util.beans.imp{
  requires org.slf4j;
  requires transitive com.github.gv2011.util;
  exports com.github.gv2011.util.beans.imp;
  provides com.github.gv2011.util.beans.TypeRegistry with com.github.gv2011.util.beans.imp.DefaultTypeRegistry;
}
