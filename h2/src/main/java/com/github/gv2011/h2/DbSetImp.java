package com.github.gv2011.h2;

import static com.github.gv2011.util.CollectionUtils.toSingle;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.icol.ICollections.upcast;
import static java.util.stream.Collectors.joining;
import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.github.gv2011.util.CloseHolder;
import com.github.gv2011.util.StringUtils;
import com.github.gv2011.util.beans.BeanType;
import com.github.gv2011.util.beans.ExtendedBeanBuilder;
import com.github.gv2011.util.beans.Property;
import com.github.gv2011.util.bytes.Hash256;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.jdbc.DbSet;
import com.github.gv2011.util.jdbc.JdbcUtils;


final class DbSetImp<B> extends AbstractCollection<B> implements DbSet<B>{

  private static final String HASH = "HASH";

  private static final Logger LOG = getLogger(DbSetImp.class);

  private final H2Database db;
  private final BeanType<B> beanType;
  private final IList<Property<?>> properties;
  private final String columnList;

  DbSetImp(final H2Database db, final BeanType<B> beanType) {
    this.db = db;
    this.beanType = beanType;
    properties = upcast(beanType.properties().values());
    final String sql =
      "CREATE TABLE IF NOT EXISTS "+db.getTableName(beanType)+"("+
        HASH+" CHARACTER("+Hash256.SIZE*2+") PRIMARY KEY, "+
        properties.stream().map(this::columnDefinition).collect(joining(", "))+
      ")"
    ;
    callWithCloseable(()->db.getConnection(), cn->{
        try(Statement stmt = cn.get().createStatement()){
          stmt.execute(sql);
        }
    });
    LOG.info("Executed {}.", sql);
    columnList = Stream
      .concat(
        Stream.of(HASH),
        properties.stream().map(this::columnName)
      )
      .collect(joining(",")
    );
  }

  private String columnDefinition(final Property<?> p) {
    return columnName(p)+" VARCHAR NOT NULL";
  }

  private String columnName(final Property<?> p) {
    return db.escapeName("p_"+p.name());
  }

  @Override
  public long longSize() {
    try(CloseHolder<Connection> cn = db.getConnection()){
      return JdbcUtils
        .executeQuery(
          cn.get(),
          "SELECT COUNT(*) FROM "+db.getTableName(beanType),
          rs->rs.getLong(1)
        )
        .collect(toSingle())
      ;
    }
  }

  @Override
  public int size() {
    final long size = longSize();
    verify(size, s->s<=Integer.MAX_VALUE);
    return (int)size;
  }

  @Override
  public boolean contains(final Object o) {
    final boolean contains;
    if(!beanType.isInstance(o)){
      contains = false;
      assert contains == super.contains(o);
    }
    else{
      try(CloseHolder<Connection> cn = db.getConnection()){
        contains = contains(cn.get(), beanType.cast(o));
      }
    }
    return contains;
  }

  private boolean contains(final Connection cn, final B o) {
    final boolean contains;
    final B element = beanType.cast(o);
    final long count = JdbcUtils
      .executeQuery(
        cn,
        "SELECT COUNT(*) FROM "+db.getTableName(beanType)+" "+
        "WHERE "+HASH+"=?",
        stmt->stmt.setString(1, elementHashToString(element)),
        rs->rs.getLong(1)
      )
      .collect(toSingle())
    ;
    verify(count, c->c.longValue()==0L || c.longValue()==1L);
    contains = count==1L;
    assert contains == super.contains(o);
    return contains;
  }

  @Override
  public Iterator<B> iterator() {
    final Iterator<B> it = stream().iterator();
    return new Iterator<B>(){
      private Opt<B> last = Opt.empty();
      @Override
      public boolean hasNext() {
         return it.hasNext();
      }
      @Override
      public B next() {
        final B next = it.next();
        last = Opt.of(next);
        return next;
      }
      @Override
      public void remove() {
        DbSetImp.this.remove(last.get());
      }
    };
  }

  @Override
  public Stream<B> stream() {
    final CloseHolder<Connection> cn = db.getConnection();
    try{
      return JdbcUtils
        .executeQuery(
          cn.get(),
          "SELECT "+columnList+" FROM "+db.getTableName(beanType),
          this::readFromResultSet
        )
        .onClose(cn::close)
      ;
    }
    catch(final Throwable t){
      cn.close(); throw t;
    }
  }

  private B readFromResultSet(final ResultSet rs) throws SQLException{
    int c = 1;
    final Hash256 hash = Hash256.parse(rs.getString(c++));
    final ExtendedBeanBuilder<B> builder = beanType.createBuilder();
    for(final Property<?> p: properties){
      setProperty(builder, p, rs.getObject(c++));
    }
    final B result = builder.build();
    verifyEqual(beanType.hashAndSize(result).hash(), hash);
    return result;
  }

  private <V> void setProperty(final ExtendedBeanBuilder<B> builder, final Property<V> p, final Object value) {
    builder.set(p, p.type().cast(value));
  }

  @Override
  public boolean add(final B e) {
    final String sql =
      "INSERT INTO "+db.getTableName(beanType)+"("+columnList+")"+
        "VALUES(?"+StringUtils.multiply(",?", beanType.properties().size())+")"
    ;
    toString(beanType.get(e, properties.get(0)));
    return callWithCloseable(()->db.getConnection(), ch->{
      final Connection cn = ch.get();
      if(contains(cn, e)) return false;
      else{
        try(PreparedStatement stmt = cn.prepareStatement(sql)){
          stmt.setString(1, elementHashToString(e));
          for(int i=0; i<properties.size(); i++){
            stmt.setString(i+2, getPropertyValue(e, properties.get(i)));
          };
          final int rows = stmt.executeUpdate();
          LOG.info("{} rows affected with {}.", rows, sql);
          verifyEqual(rows, 1);
          cn.commit();
          return true;
        }
      }
    });
  }

  private String elementHashToString(final B e){
    return hashToString(beanType.hashAndSize(e).hash());
  }

  private String hashToString(final Hash256 hash){
    return hash.content().toHex();
  }

  private String getPropertyValue(final B e, final Property<?> p) {
    return toString(beanType.get(e, p));
  }

  private String toString(final Object o){
    return (String)o;
  }

  @Override
  public boolean remove(final Object o) {
    final boolean contained;
    if(!beanType.isInstance(o)){
      contained = false;
      assert contained == super.contains(o);
    }
    else{
      final B element = beanType.cast(o);
      final String sql =
        "DELETE FROM "+db.getTableName(beanType)+" WHERE "+HASH+"=?"
      ;
      contained = callWithCloseable(db::getConnection, cn->{
        try(PreparedStatement stmt = cn.get().prepareStatement(sql)){
          stmt.setString(1, elementHashToString(element));
          final int rows = stmt.executeUpdate();
          LOG.info("{} rows affected with {}.", rows, sql);
          verify(rows, r->r.intValue()==0 || r.intValue()==1);
          cn.get().commit();
          return rows > 0;
        }
      });
    }
    return contained;
  }

  @Override
  public Opt<B> tryGet(final Hash256 hash) {
      final String sql =
        "SELECT "+columnList+" FROM "+db.getTableName(beanType)+" WHERE "+HASH+"=?"
      ;
      return callWithCloseable(db::getConnection, cn->{
        try(PreparedStatement stmt = cn.get().prepareStatement(sql)){
          stmt.setString(1, hashToString(hash));
          try(final ResultSet rs = stmt.executeQuery()){
            final Opt<B> result;
            if(!rs.next()) result = Opt.empty();
            else{
              result = Opt.of(readFromResultSet(rs));
              verify(!rs.next());
            }
            return result;
          }
        }
      });
   }


}
