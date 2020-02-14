package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.ex.Exceptions.notYetImplemented;

import java.io.InputStream;

final class ReversedBytes extends AbstractBytes{

  private final Bytes original;

  ReversedBytes(final Bytes original) {
    this.original = original;
  }

  @Override
  public byte get(final long index) {
    return original.get(translateIndex(index));
  }

  @Override
  public long longSize() {
    return original.longSize();
  }

  @Override
  public InputStream openStream() {
    return notYetImplemented();
  }

  @Override
  public Bytes subList(final long fromIndex, final long toIndex) {
    return original.subList(translateIndex(toIndex), translateIndex(fromIndex)).reversed();
  }

  @Override
  public Bytes reversed() {
    return original;
  }

  private long translateIndex(final long index){
    return original.get(original.longSize()-(index+1));
  }

}
