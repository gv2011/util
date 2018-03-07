package com.github.gv2011.util.beans.cglib;

public abstract class TestModel {

  public abstract Integer number1();

  public abstract Long number2();

  public Long sum(){
    return number1() + number2();
  }

}
