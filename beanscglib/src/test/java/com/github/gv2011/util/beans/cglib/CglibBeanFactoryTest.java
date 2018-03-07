package com.github.gv2011.util.beans.cglib;

import static com.github.gv2011.testutil.Matchers.is;
import static com.github.gv2011.util.json.JsonUtils.jsonFactory;
import static org.junit.Assert.*;

import org.junit.Test;

import com.github.gv2011.util.beans.imp.DefaultTypeRegistry;

public class CglibBeanFactoryTest {

  @Test
  public void test() {
    final DefaultTypeRegistry reg = new DefaultTypeRegistry(jsonFactory(), new CglibBeanFactoryBuilder());
    final TestModel adder = reg.createBuilder(TestModel.class).build();
    assertThat(adder.sum(), is(0L));
  }

}
