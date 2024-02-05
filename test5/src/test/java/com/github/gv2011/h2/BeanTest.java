package com.github.gv2011.h2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static com.github.gv2011.util.ReflectionUtils.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.beans.KeyBean;

class BeanTest {

  @Test
  @Disabled("wip")
  void test() {
    assertTrue(inherits(method(Name.class, KeyBean::key), method(KeyBean.class, KeyBean::key)));

    BeanUtils.beanBuilder(PersonalId.class).build();

    final Name account = BeanUtils.beanBuilder(Name.class)
      .set(Name::organisation).to("Digitalcourage e. V.")
      .set(Name::number).to(42L)
      .set(Name::givenName).to("Hanna")
      .set(Name::surname).to("Müller")
      .build()
    ;

    final PersonalId id = BeanUtils.beanBuilder(PersonalId.class)
      .set(PersonalId::organisation).to("Digitalcourage e. V.")
      .set(PersonalId::number).to(42L)
      .build()
    ;

    assertThat(
      account.toString(),
      is("Name{givenName=Hanna, number=42, organisation=Digitalcourage e. V., surname=Müller}")
    );

    assertThat(
      account.key(),
      is(id)
    );


  }

}
