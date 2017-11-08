package com.github.gv2011.util.streams;

/*-
 * %---license-start---
 * The MIT License (MIT)
 * %
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %
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
 * %---license-end---
 */



import java.util.Optional;
import java.util.function.Consumer;

import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.streams.StreamEvent.State;

final class MarkerFilter implements Consumer<StreamEvent> {

  private final Consumer<StreamEvent> sink;
  private final Bytes marker;

  private Bytes buffer = ByteUtils.emptyBytes();

  MarkerFilter(final Consumer<StreamEvent> sink, final Bytes marker) {
    this.sink = sink;
    this.marker = marker;
  }

  @Override
  public void accept(final StreamEvent e) {
    final State state = e.state();
    if(state==State.CANCELLED){
      sink.accept(e);
    }else{
      buffer = buffer.append(e.data());
      final Optional<Long> idx = buffer.indexOfOther(marker);
      if(idx.isPresent()){
        final long afterMarker = idx.get()+marker.longSize();
        if(state==State.DATA || afterMarker<buffer.longSize()){
          final Bytes forward = buffer.subList(0L, afterMarker);
          buffer = buffer.subList(afterMarker, buffer.longSize());
          sink.accept(StreamEventImp.data(forward));
        }
      }
      if(state==State.EOS){
        sink.accept(StreamEventImp.eos(buffer));
      }else if(state==State.ERROR){
        sink.accept(StreamEventImp.error(e.error(), buffer));
      }
    }
  }

}
