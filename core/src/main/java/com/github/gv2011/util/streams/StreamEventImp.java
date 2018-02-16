package com.github.gv2011.util.streams;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */




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
