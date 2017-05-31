package com.github.gv2011.util.net;

import static com.github.gv2011.util.ex.Exceptions.call;

import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.bytes.BytesBuilder;
import com.github.gv2011.util.ex.ThrowingSupplier;

public final class StreamEvent {


  public static final Thread process(
    final ThrowingSupplier<InputStream> stream, final Consumer<StreamEvent> eventHandler
  ){
    return new StreamProcessor(call(stream::get), eventHandler, ByteUtils.emptyBytes()).thread;
  }

  public static final Thread process(
    final ThrowingSupplier<InputStream> stream, final Consumer<StreamEvent> eventHandler, final Bytes marker
  ){
    return new StreamProcessor(call(stream::get), eventHandler, marker).thread;
  }

  private final boolean eof;
  private final Bytes data;
  private final Optional<Throwable> exception;


  private StreamEvent(final boolean eof, final Bytes data, final Optional<Throwable> exception) {
    this.eof = eof;
    this.data = data;
    this.exception = exception;
  }

  public Bytes data() {
    exception.ifPresent(e->{throw new RuntimeException(e);});
    return data;
  }

  public Optional<Throwable> exception() {
    return exception;
  }

  public boolean closed(){
    exception.ifPresent(e->{throw new RuntimeException(e);});
    return eof;
  }


  @Override
  public String toString() {
    return closed() ? "-closed-" : data.toString();
  }

  private static class StreamProcessor{

    private final Thread thread;
    private final InputStream stream;
    private final Consumer<StreamEvent> eventHandler;
    private final Bytes marker;
    private final BytesBuilder store;

    private StreamProcessor(final InputStream stream, final Consumer<StreamEvent> eventHandler, final Bytes marker) {
      this.stream = stream;
      this.eventHandler = eventHandler;
      this.marker = marker;
      store = ByteUtils.newBytesBuilder();
      thread = Executors.defaultThreadFactory().newThread(this::process);
      thread.start();
    }

    private void process() {
      try{
        try{
          final byte[] buffer = new byte[8192];
          int count = stream.read(buffer);
          while(count!=-1){
            if(count>0){
              store.write(buffer, 0, count);
              final Optional<Bytes> found = store.remove(marker);
              found.ifPresent(b->
                eventHandler.accept(new StreamEvent(false, b, Optional.empty()))
              );
            }
            count = stream.read(buffer);
          }
        }
        finally{
          stream.close();
        }
      }catch(final Throwable e){
        eventHandler.accept(new StreamEvent(false, ByteUtils.emptyBytes(), Optional.of(e)));
      }finally{
        eventHandler.accept(new StreamEvent(true, store.build(), Optional.empty()));
      }
    }

  }

}
