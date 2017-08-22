package com.github.gv2011.util.streams;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;

import java.util.Arrays;
import java.util.Optional;

import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;

final class StreamEventImp implements StreamEvent{

  private final Bytes bytes;
  private final State state;
  private final Optional<Throwable> error;

  private StreamEventImp(final State state, final Bytes bytes, final Optional<Throwable> error) {
    if(error.isPresent()) verifyEqual(state, State.ERROR);
    if(state==State.CANCELLED) verify(bytes.isEmpty());
    this.state = state;
    this.bytes = bytes;
    this.error = error;
  }

  @Override
  public State state() {
    return state;
  }

  @Override
  public Bytes data() {
    return bytes;
  }

  @Override
  public Throwable error(){
    return error.get();
  }

  public static StreamEvent data(final Bytes data) {
    return new StreamEventImp(State.DATA, data, Optional.empty());
  }

  public static StreamEvent eos(final Bytes data) {
    return new StreamEventImp(State.EOS, data, Optional.empty());
  }

  public static StreamEvent error(final Throwable error, final Bytes data) {
    return new StreamEventImp(State.ERROR, data, Optional.of(error));
  }

  public static StreamEvent cancelled() {
    return new StreamEventImp(State.CANCELLED, ByteUtils.emptyBytes(), Optional.empty());
  }

  @Override
  public String toString() {
    return StreamEvent.class.getSimpleName()
      + Arrays.toString(error.isPresent() ? new Object[]{state, bytes, error.get()} : new Object[]{state, bytes})
    ;
  }


}
