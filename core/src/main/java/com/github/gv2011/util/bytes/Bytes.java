package com.github.gv2011.util.bytes;

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

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.github.gv2011.util.OptCloseable;
import com.github.gv2011.util.Pair;
import com.github.gv2011.util.ann.Immutable;
import com.github.gv2011.util.uc.UStr;


@Immutable
public interface Bytes extends List<Byte>, Comparable<Bytes>, OptCloseable{

  public static final class TooBigException extends IllegalStateException {}

  Bytes append(Bytes hashBytes);

  Hash256 asHash();

  Bytes decodeBase64();

  byte get(long index);

  int getUnsigned(long index);

  Hash256 hash();

  Optional<Long> indexOfOther(Bytes other);

  long longSize();

  InputStream openStream();

  @Override
  int size() throws TooBigException;

  Pair<Bytes,Bytes> split(long index);

  boolean startsWith(Bytes prefix);

  @Override
  Bytes subList(int fromIndex, int toIndex);

  Bytes subList(final long fromIndex, final long toIndex);

  Bytes toBase64();

  byte[] toByteArray() throws TooBigException;

  String toHexMultiline();

  String toHex();

  String toHexColon();

  int toInt();

  String toString(Charset charset);

  String utf8ToString() throws TooBigException;

  UStr utf8ToUStr() throws TooBigException;

  int write(byte[] b, int off, int len);

  void write(final OutputStream stream);

  void write(final Path file);

  byte getByte(int i);

  Bytes subList(int fromIndex);

  TypedBytes typed();

  TypedBytes typed(DataType mimeType);



}
