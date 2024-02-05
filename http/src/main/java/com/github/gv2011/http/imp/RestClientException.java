package com.github.gv2011.http.imp;

public class RestClientException extends RuntimeException{

  RestClientException(final Throwable t) {
    super(t);
  }

  RestClientException(final String msg, final Throwable t) {
    super(msg, t);
  }

}
