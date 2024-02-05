package com.github.gv2011.h2;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.closeAll;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.pathFrom;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static java.util.stream.Collectors.joining;
import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

import org.slf4j.Logger;

import com.github.gv2011.util.CloseHolder;
import com.github.gv2011.util.beans.BeanType;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.jdbc.Database;
import com.github.gv2011.util.jdbc.JdbcUtils;
import com.github.gv2011.util.table.Table;
import com.github.gv2011.util.tempfile.TempDir;

public final class H2Database implements Database{

  private static final Logger LOG = getLogger(H2Database.class);

  private final Object lock = new Object();
  private final TempDir tempDir;
  private final Connection connection;

  private boolean closed;


  H2Database(final TempDir tempDir){
    this.tempDir = tempDir;
    LOG.debug("Creating database in {}.", tempDir);
    connection = call(()->DriverManager.getConnection(url()));
    call(()->connection.setAutoCommit(false));
  }

  String url() {
    return format("jdbc:h2:file:/{}/h2", pathFrom(tempDir.path()).stream().collect(joining("/")));
  }

  private void checkOpen() {
    synchronized(lock) {
      verify(!closed);
    }
  }

  @Override
  public void close() {
    synchronized(lock) {
      if(!closed) {
        closed = true;
        closeAll(connection, tempDir);
      }
    }
  }

  @Override
  public <B> DbSetImp<B> createSet(final BeanType<B> beanType) {
    checkOpen();
    return new DbSetImp<>(this, beanType);
  }

  CloseHolder<Connection> getConnection() {
    return CloseHolder.dontClose(connection);
  }

  ISet<String> getCatalogs(){
    return callWithCloseable(()->getConnection(), cn->{
      return JdbcUtils
        .stream(cn.get().getMetaData().getCatalogs(), rs->rs.getString(1))
        .collect(toISet())
      ;
    });
  }

  String getDefaultCatalog(){
    return "H2";
  }

  public ISet<String> getSchemas(final String catalog) {
    return callWithCloseable(()->getConnection(), cn->{
      return JdbcUtils
        .stream(cn.get().getMetaData().getSchemas(catalog, null), rs->rs.getString(1))
        .collect(toISet())
      ;
    });
  }

  public String getDefaultSchema() {
    return "PUBLIC";
  }

  public ISet<String> getTables() {
    return getTables(getDefaultCatalog(), getDefaultSchema());
  }

  public Table<String> getTableInfo(){
    return callWithCloseable(this::getConnection, cn->{
      final DatabaseMetaData metaData = cn.get().getMetaData();
      try(ResultSet rs = metaData.getTables(getDefaultCatalog(), getDefaultSchema(), null, null)){
        return JdbcUtils.convertToTable(rs);
      }
    });
  }

  public Table<String> getColumnInfo(){
    return callWithCloseable(this::getConnection, cn->{
      final DatabaseMetaData metaData = cn.get().getMetaData();
      try(ResultSet rs = metaData.getColumns(getDefaultCatalog(), getDefaultSchema(), null, null)){
        return JdbcUtils.convertToTable(rs);
      }
    });
  }

  public ISet<String> getTables(final String catalog, final String schema) {
    return callWithCloseable(()->getConnection(), cn->{
      return JdbcUtils
        .stream(cn.get().getMetaData().getTables(catalog, schema, null, null), rs->rs.getString(3))
        .collect(toISet())
      ;
    });
  }

  String getTableName(final BeanType<?> type){
    //return StringUtils.lastPart(type.name(), '.').toUpperCase(Locale.ROOT);
    return escapeName(type.name());
  }

  String escapeName(final String name){
    return "\""+(name.replace("\"", "\"\""))+"\"";
  }

}
