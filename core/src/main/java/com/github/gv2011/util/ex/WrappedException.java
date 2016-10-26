package com.github.gv2011.util.ex;

public class WrappedException extends RuntimeException{

  WrappedException(final Throwable cause) {
    super(cause);
  }

  WrappedException(final Throwable cause, final String msg) {
    super(msg, cause);
  }

}
