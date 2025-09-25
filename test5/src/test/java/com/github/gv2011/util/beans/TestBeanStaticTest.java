package com.github.gv2011.util.beans;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.icol.Opt;

public class TestBeanStaticTest {

  @Test
  void testProperty1() {
    final TypeRegistry registry = BeanUtils.typeRegistry();
    final BeanType<TestBeanStatic> beanType = registry.beanType(TestBeanStatic.class);
    assertThat(beanType.properties().single().getKey(), is("property1"));
    final Property<?> p = beanType.properties().single().getValue();
    assertThat(p.name(), is("property1"));
    assertThat(p.defaultValue(), is(Opt.of("")));
    assertThat(p.fixedValue(), is(Opt.empty()));
    assertThat(p.isKey(), is(false));
    assertThat(p.type().name(), is(String.class.getName()));
  }

}
