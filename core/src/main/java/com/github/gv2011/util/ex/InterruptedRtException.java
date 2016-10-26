package com.github.gv2011.util.ex;

public class InterruptedRtException extends RuntimeException{

  public InterruptedRtException(final Throwable e, final String msg) {
    super(msg, e);
  }

}
