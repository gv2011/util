package com.github.gv2011.util.bytes;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
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
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

final class JoiningBytesCollector implements Collector<Bytes, BytesBuilder, Bytes> {

  private static final Set<Characteristics> CHARACTERISTICS =
    Collections.unmodifiableSet(EnumSet.of(Characteristics.CONCURRENT))
  ;

  @Override
  public Supplier<BytesBuilder> supplier() {
    return BytesBuilder::new;
  }

  @Override
  public BiConsumer<BytesBuilder, Bytes> accumulator() {
    return (builder, bytes)->builder.append(bytes);
  }

  @Override
  public BinaryOperator<BytesBuilder> combiner() {
    return (b1,b2)->b1.append(b2.build());
  }

  @Override
  public Function<BytesBuilder, Bytes> finisher() {
    return BytesBuilder::build;
  }

  @Override
  public Set<Characteristics> characteristics() {
    return CHARACTERISTICS;
  }

}
