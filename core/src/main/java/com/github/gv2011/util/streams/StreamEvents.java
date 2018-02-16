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

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.notYetImplemented;

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
      if(result==null) call(s::close);
    }
    return result;
  }

  public static final Consumer<StreamEvent> markerFilter(final Consumer<StreamEvent> sink, final Bytes marker){
    return new MarkerFilter(sink, marker);
  }

}
