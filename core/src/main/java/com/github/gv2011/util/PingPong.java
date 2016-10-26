package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.run;

public class PingPong {

  private final Object lock = new Object();
  private boolean ping = true;

  public void ping(){
    synchronized(lock){
      while(!ping) run(()->lock.wait());
      ping = false;
      lock.notifyAll();
    }
  }

  public void pong(){
    synchronized(lock){
      while(ping) run(()->lock.wait());
      ping = true;
      lock.notifyAll();
    }
  }

}
