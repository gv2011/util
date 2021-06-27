package com.github.gv2011.util.beans;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.github.gv2011.util.BeanUtils;

class AbstractBeanTest {

  @Test
  void testHashCode() {
    final AbstractBeanTestBean b1 = create();
    final AbstractBeanTestBean b2 = create();
    assertFalse(b1==b2);
    assertThat(b1.hashCode(), is(b2.hashCode()));
  }

  @Test
  void testEquals() {
    final AbstractBeanTestBean b1 = create();
    final AbstractBeanTestBean b2 = create();
    assertFalse(b1==b2);
    assertThat(b1, is(b2));
  }

  @Test
  void testToString() {
    final AbstractBeanTestBeanImp b1 = create();
    assertThat(b1.toString(), is("Hauptstraße 25"));
    assertThat(b1.superToString(), is("AbstractBeanTestBean{number=25, street=Hauptstraße}"));
  }

  @Test
  void testBuild() {
    final AbstractBeanTestBean b1 = create();
    final AbstractBeanTestBean b2 = BeanUtils.beanBuilder(AbstractBeanTestBean.class)
      .set(AbstractBeanTestBean::street).to(b1.street())
      .set(AbstractBeanTestBean::number).to(b1.number())
      .build()
    ;
    assertThat(b2,is(b1));
    assertThat(b2.hashCode(),is(b1.hashCode()));
    assertTrue(Proxy.isProxyClass(b2.getClass()));
  }

  @Test
  void testBuild2() {
    final AbstractBeanTestBean2 b1 = create2();
    final AbstractBeanTestBean2 b2 = BeanUtils.beanBuilder(AbstractBeanTestBean2.class)
      .set(AbstractBeanTestBean2::street).to(b1.street())
      .set(AbstractBeanTestBean2::number).to(b1.number())
      .set(AbstractBeanTestBean2::date).to(b1.date())
      .build()
    ;
    assertThat(b2,is(b1));
    assertThat(b2.hashCode(),is(b1.hashCode()));
    assertThat(b2.getClass(), is(AbstractBeanTestBean2Imp.class));
  }

  private AbstractBeanTestBeanImp create() {
    return new AbstractBeanTestBeanImp("Hauptstraße", 25);
  }

  private AbstractBeanTestBean2Imp create2() {
    return new AbstractBeanTestBean2Imp("Hauptstraße", Instant.parse("2021-06-27T11:49:02Z"));
  }

}
