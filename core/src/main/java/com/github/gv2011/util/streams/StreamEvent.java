package com.github.gv2011.util.streams;

import com.github.gv2011.util.bytes.Bytes;

public interface StreamEvent {

  public static enum State{DATA,EOS,CANCELLED,ERROR;
    public boolean finished(){return this!=DATA;}
  }

  State state();

  Bytes data();

  Throwable error();

}
