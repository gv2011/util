package com.github.gv2011.util.bytes;

import java.io.InputStream;
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

  CloseableBytes toBase64();

  CloseableBytes decodeBase64();

  InputStream openStream();

  void write(final OutputStream stream);

  void write(final Path file);

  @Override
  Bytes subList(int fromIndex, int toIndex);

  Bytes subList(final long fromIndex, final long toIndex);

  Hash256 hash();

  Hash256 asHash();

  int toInt();

  public static final class TooBigException extends IllegalStateException {}

  Bytes toHexMultiline();

  Bytes append(Bytes hashBytes);

  boolean startsWith(Bytes prefix);

  long indexOfOther(Bytes other);

}
