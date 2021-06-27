package com.github.gv2011.util.email.imp;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.DataTypes;
import com.github.gv2011.util.email.Email;

public class EmailTest {
  
  @Test
  public void test(){
    final Email email = (Email) ByteUtils.readTyped(getClass().getResource("mail1.eml"));
    assertThat(email.content().longSize(), is(812L));
    assertThat(email.dataType(), is(DataTypes.MESSAGE_RFC822));
  }

}
