package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.run;

import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.ann.Nullable;

public class StreamIterator implements Iterator<Byte>, AutoCloseableNt{

  private @Nullable InputStream stream;
  private int next;

  StreamIterator(final InputStream stream) {
    this.stream = stream;
    readNext();
  }

  private void readNext() {
    final InputStream stream = this.stream;
    if(stream==null) throw new IllegalStateException("Closed.");
    next = call(()->stream.read());
    if(!hasNext()) close();
  }

  @Override
  public void close() {
    final InputStream stream = this.stream;
    if(stream!=null){
      run(()->stream.close());
      this.stream = null;
    }
  }

  @Override
  public boolean hasNext() {
    return next!=-1;
  }

  @Override
  public Byte next() {
    if(next==-1)throw new NoSuchElementException();
    final byte result = (byte)next;
    readNext();
    return result;
  }

}
