package com.github.gv2011.h2;

import static com.github.gv2011.util.icol.ICollections.emptySet;
import static com.github.gv2011.util.icol.ICollections.setOf;
import static com.github.gv2011.util.icol.ICollections.single;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.sql.SQLException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.beans.BeanType;
import com.github.gv2011.util.bytes.HashAndSize;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.jdbc.JdbcUtils;
import com.github.gv2011.util.table.Table;

class H2DatabaseTest {

  @Test
  void test() {
    try(H2Database db = (H2Database) JdbcUtils.createDatabase()){
      assertThat(db.getCatalogs(), is(single("H2")));
      assertThat(db.getSchemas("H2"), is(setOf("INFORMATION_SCHEMA", "PUBLIC")));
      assertThat(db.getTables("H2", "PUBLIC"), is(emptySet()));
      assertThat(db.getTables(), is(emptySet()));
    }
  }

  @Test
  @Disabled("wip")
  void testSet() throws SQLException {
    try(H2Database db = (H2Database) JdbcUtils.createDatabase()){
      final BeanType<Name> beanType = BeanUtils.typeRegistry().beanType(Name.class);
      final String tableName = db.getTableName(beanType);
      assertThat(tableName,       is("\"com.github.gv2011.h2.Name\""));
      assertThat(beanType.name(), is(  "com.github.gv2011.h2.Name"  ));

      final DbSetImp<Name> dbSet = db.createSet(beanType);
      assertThat(db.getTables("H2", "PUBLIC"), is(setOf(beanType.name())));
      assertThat(db.getTables(),               is(setOf(beanType.name())));

      System.out.println(db.getTableInfo().formatted());
      System.out.println(db.getColumnInfo().formatted());

      assertThat(dbSet.size(), is(0));
      final Name obj = beanType.createBuilder()
        .set(Name::givenName).to("Hanna")
        .set(Name::surname).to("Müller")
        .build()
      ;
      final HashAndSize hashAndSize = beanType.hashAndSize(obj);
      assertThat(hashAndSize.size(), is(41L));
      assertThat(
        hashAndSize.hash().content().toHex(),
        is("498db11bd770155dd738e3d2c9a423cfa043dcfaa2c0a4536ddba26480dd22f5")
      );

      assertThat(dbSet.contains(obj), is(false));
      assertThat(dbSet.tryGet(hashAndSize.hash()), is(Opt.empty()));

      boolean added = dbSet.add(obj);
      assertThat(added, is(true));
      assertThat(dbSet.size(), is(1));
      assertThat(dbSet.iterator().next(), is(obj));
      assertThat(dbSet.contains(obj), is(true));
      assertThat(dbSet.tryGet(hashAndSize.hash()), is(Opt.of(obj)));


      final Table<String> table = JdbcUtils.executeQuery(
        db::getConnection,
        "SELECT * FROM "+tableName,
        JdbcUtils::convertToTable
      );
      System.out.println(table.formatted());

      added = dbSet.add(obj);
      assertThat(added, is(false));
      assertThat(dbSet.size(), is(1));
      assertThat(dbSet.iterator().next(), is(obj));
      assertThat(dbSet.contains(obj), is(true));

      final boolean removed = dbSet.remove(obj);
      assertThat(removed, is(true));
      assertThat(dbSet.size(), is(0));
      assertThat(dbSet.iterator().hasNext(), is(false));
      assertThat(dbSet.contains(obj), is(false));
    }
  }

  @Test
  void testName() {
    final BeanType<Name> beanType = BeanUtils.typeRegistry().beanType(Name.class);
    final Name name = beanType.createBuilder()
      .set(Name::givenName).to("Hanna")
      .set(Name::surname).to("Müller")
      .build()
    ;
    System.out.println(
      beanType.toJson(name).hash().hash().content().toHex()
    );
  }

}
