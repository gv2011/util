package com.github.gv2011.util.streams;

import static com.github.gv2011.util.ex.Exceptions.call;

import java.io.InputStream;
import java.io.OutputStream;

import com.github.gv2011.util.AutoCloseableNt;

public interface Connection extends AutoCloseableNt{

  InputStream input();

  OutputStream output();

  @Override
  default void close(){
    call(input()::close);
  }

}
