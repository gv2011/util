package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.run;

final class ProtectedThread implements ThreadFacade{

  private final Thread thread;

  ProtectedThread(final Thread thread){
    this.thread = thread;
  }

  @Override
  public boolean isAlive() {
    return thread.isAlive();
  }

  @Override
  public void interrupt() {
    thread.interrupt();
  }

  @Override
  public void join() {
    run(thread::join);
  }



}
