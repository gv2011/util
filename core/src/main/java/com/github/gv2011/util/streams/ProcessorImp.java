package com.github.gv2011.util.streams;

import static com.github.gv2011.util.ex.Exceptions.notYetImplementedException;
import static com.github.gv2011.util.ex.Exceptions.run;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import com.github.gv2011.util.bytes.ByteUtils;

//TODO WIP
final class ProcessorImp implements Future<Long>{

  private final Thread thread;
  private final InputStream stream;
  private final Consumer<StreamEvent> eventHandler;
  private final Object lock = new Object();
  private boolean cancelled;
  private boolean done;

  ProcessorImp(final InputStream stream, final Consumer<StreamEvent> eventHandler) {
    this.stream = stream;
    this.eventHandler = eventHandler;
    thread = Executors.defaultThreadFactory().newThread(this::process);
    thread.start();
  }

  private void process() {
    try{
      try{
        final byte[] buffer = new byte[8192];
        final int validInBuffer = 0;
        long count = 0;
        final boolean done = false;
        while(!done){
          if(count>0){
            eventHandler.accept(StreamEventImp.data(ByteUtils.newBytes(buffer, 0, (int)count)));
            count = 0;
          }
          if(cancelled) count = -1;
          else{
            try{count = stream.read(buffer);}
            catch(final IOException e){
              count = -1;
              if(!cancelled) throw e; //assuming the exception is consequence of cancelling.
            }
          }
        }
        //eventHandler.accept(cancelled?StreamEventImp.cancelled() : StreamEventImp.eos(ByteUtils.emptyBytes()));
      }
      finally{
        stream.close();
      }
    }catch(final Throwable e){
      eventHandler.accept(StreamEventImp.error(e, ByteUtils.emptyBytes()));
    }
  }



  @Override
  public boolean isCancelled() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public boolean isDone() {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Long get() throws InterruptedException, ExecutionException {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

  @Override
  public Long get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    // TODO Auto-generated method stub
    throw notYetImplementedException();
  }

    @Override
    public boolean cancel(final boolean mayInterrupt) {
      cancelled = true;
      run(stream::close);
      return false;
    }
}
