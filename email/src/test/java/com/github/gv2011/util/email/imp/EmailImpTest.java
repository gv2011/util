package com.github.gv2011.util.email.imp;

import org.junit.Test;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.email.Email;

public class EmailImpTest {

  @Test
  public void test() {
    BeanUtils.beanBuilder(Email.class).build();
  }

}
