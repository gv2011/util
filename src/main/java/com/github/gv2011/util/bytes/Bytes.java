package com.github.gv2011.util.bytes;

import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

import net.jcip.annotations.Immutable;

@Immutable
public interface Bytes extends List<Byte>{

  @Override
  int size() throws TooBigException;

  long longSize();

  byte get(long index);

  byte[] toByteArray() throws TooBigException;

  String utf8ToString() throws TooBigException;

  void write(final OutputStream stream);

  void write(final Path file);

  @Override
  Bytes subList(int fromIndex, int toIndex);

  Bytes subList(final long fromIndex, final long toIndex);

  Hash256 hash();


  public static final class TooBigException extends IllegalStateException {}

}
