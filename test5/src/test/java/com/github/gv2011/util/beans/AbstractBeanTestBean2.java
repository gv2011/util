package com.github.gv2011.util.beans;

import java.time.Instant;

@Final(implementation=AbstractBeanTestBean2Imp.class)
public interface AbstractBeanTestBean2 extends Bean{

  String street();

  Integer number();

  Instant date();

}
