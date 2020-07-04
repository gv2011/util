package com.github.gv2011.util.streams;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.ExecutorUtils;

public class Server implements AutoCloseableNt{

  private final ExecutorService executor = Executors.newCachedThreadPool();
  private volatile boolean closed;

  public Server(
    final ConnectionListener connectionListener,
    final Function<Connection,AutoCloseableNt> connectionHandler
  ){
    executor.submit(this::run);
  }

  private void run(){
    while(!closed){

    }
  }

  @Override
  public void close() {
    closed = true;
    ExecutorUtils.asCloseable(executor).close();
  }

  public static interface ConnectionListener extends AutoCloseableNt{
    Connection accept();
  }


  public static interface ConnectionHandler extends AutoCloseableNt{
    Connection handleConnection();
  }

}
