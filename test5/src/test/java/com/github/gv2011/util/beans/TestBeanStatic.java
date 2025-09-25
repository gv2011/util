package com.github.gv2011.util.beans;

public interface TestBeanStatic extends Bean{

  /**
   * This static method must not be considered a property.
   */
  static String staticMethod1(){return "staticMethod1ReturnValue";}

  /**
   * Simple property (at least one property must exist.)
   */
  String property1();

}
