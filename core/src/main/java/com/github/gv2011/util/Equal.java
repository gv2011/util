package com.github.gv2011.util;

public final class Equal {

  public static final boolean equal(final Object o1, final Object o2){
    boolean result;
    if(o1==o2) result = true;
    else if(o1==null) result = false;
    else {
      result = o1.equals(o2);
      assert result==o2.equals(o1) : "Inconsistent equals implementation.";
    }
    return result;
  }

}
