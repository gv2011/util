package com.github.gv2011.util.context;

public interface Context {

  public static <I> ContextConstant<I> createConstant(final Class<I> interfaze){
    return null;
  }

  public static Context getContextOfCurrentThread() {
    return null;
  }

}
