package com.github.gv2011.util.beans.imp;

import org.junit.Test;

import com.github.gv2011.util.XStream;
import com.github.gv2011.util.tstr.TypedString;

public class TypedStringInvocationHandlerTest {

  static interface StringA extends TypedString<StringA>{}

  @Test
  public void test() {
    XStream.of(StringA.class.getMethods()).forEach(System.out::println);
  }

}
