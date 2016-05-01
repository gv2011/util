package com.github.gv2011.util.ex;

class Bug extends RuntimeException{

private static final long serialVersionUID = -4734047359228580654L;

Bug() {}

Bug(final String message, final Throwable cause) {
  super(message, cause);
}

Bug(final String message) {
  super(message);
}

Bug(final Throwable cause) {
  super(cause);
}


}
