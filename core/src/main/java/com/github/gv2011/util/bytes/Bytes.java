package com.github.gv2011.util.bytes;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.github.gv2011.util.OptCloseable;
import com.github.gv2011.util.Pair;

import net.jcip.annotations.Immutable;

@Immutable
public interface Bytes extends List<Byte>, OptCloseable{

  @Override
  int size() throws TooBigException;

  long longSize();

  byte get(long index);

  byte[] toByteArray() throws TooBigException;

  String utf8ToString() throws TooBigException;

  Bytes toBase64();

  Bytes decodeBase64();

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

  Optional<Long> indexOfOther(Bytes other);

  Pair<Bytes,Bytes> split(long index);

  int write(byte[] b, int off, int len);

  String toString(Charset charset);



}
