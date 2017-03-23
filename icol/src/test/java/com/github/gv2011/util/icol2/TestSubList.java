package com.github.gv2011.util.icol2;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class TestSubList {

  @Test
  public void test() {
    final ArrayList<Object> l = new java.util.ArrayList<>();
    System.out.println(l.subList(-5, 5).size());
  }

}
