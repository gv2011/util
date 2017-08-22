package com.github.gv2011.util.streams;

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
