module com.github.gv2011.http{
  requires transitive com.github.gv2011.util;
  requires java.sql;
  requires com.h2database;
  requires org.slf4j;

  exports com.github.gv2011.h2 to com.github.gv2011.util;

  provides com.github.gv2011.util.jdbc.DbProvider with com.github.gv2011.h2.H2DbProvider;
}