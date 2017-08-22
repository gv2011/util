package com.github.gv2011.util.streams;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.notYetImplemented;
import static com.github.gv2011.util.ex.Exceptions.run;

import java.io.InputStream;
import java.util.function.Consumer;

import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.ex.ThrowingSupplier;

public final class StreamEvents {

  public static final Processor process(
    final ThrowingSupplier<InputStream> stream, final Consumer<StreamEvent> eventHandler
  ){
    @Nullable Processor result = null;
    final InputStream s = call(stream::get);
    try{
//      result = new ProcessorImp(s, eventHandler);
      result = notYetImplemented();
    }finally{
      if(result==null) run(s::close);
    }
    return result;
  }

  public static final Consumer<StreamEvent> markerFilter(final Consumer<StreamEvent> sink, final Bytes marker){
    return new MarkerFilter(sink, marker);
  }

}
