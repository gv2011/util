package com.github.gv2011.h2;

import static com.github.gv2011.util.icol.ICollections.emptySet;
import static com.github.gv2011.util.icol.ICollections.setOf;
import static com.github.gv2011.util.icol.ICollections.single;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.beans.BeanType;
import com.github.gv2011.util.jdbc.JdbcUtils;

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
  void testSet() throws SQLException {
    try(H2Database db = (H2Database) JdbcUtils.createDatabase()){
      final BeanType<Name> beanType = BeanUtils.typeRegistry().beanType(Name.class);
      assertThat(beanType.name(), is("com.github.gv2011.h2.Name"));
      final DbSetImp<Name> dbSet = db.createSet(beanType);
      assertThat(db.getTables("H2", "PUBLIC"), is(setOf(beanType.name())));
      assertThat(db.getTables(), is(setOf(beanType.name())));
      assertThat(dbSet.size(), is(0));
      final Name obj = beanType.createBuilder()
        .set(Name::givenName).to("Hanna")
        .set(Name::surname).to("Müller")
        .build()
      ;
      assertThat(dbSet.contains(obj), is(false));

      boolean added = dbSet.add(obj);
      assertThat(added, is(true));
      assertThat(dbSet.size(), is(1));
      assertThat(dbSet.iterator().next(), is(obj));
      assertThat(dbSet.contains(obj), is(true));

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
