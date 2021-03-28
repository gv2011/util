package com.github.gv2011.util.json.imp;

/*-
 * #%L
 * jsong
 * %%
 * Copyright (C) 2017 Vinz (https://github.com/gv2011)
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



import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IList.Builder;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.json.JsonList;
import com.github.gv2011.util.json.JsonNode;

final class JsonListCollector implements Collector<JsonNode, IList.Builder<JsonNode>, JsonList> {

  private final JsonFactoryImp f;

  JsonListCollector(final JsonFactoryImp f) {
    this.f = f;
  }

  @Override
  public BiConsumer<Builder<JsonNode>, JsonNode> accumulator() {
    return (b,e)->b.add((JsonNode)e);
  }

  @Override
  public ISet<Characteristics> characteristics() {
    return f.listCharacteristics;
  }

  @Override
  public BinaryOperator<Builder<JsonNode>> combiner() {
    return (b1,b2)->b1.addAll(b2.build());
  }

  @Override
  public Function<Builder<JsonNode>, JsonList> finisher() {
    return b->new JsonListImp(f, b.build());
  }

  @Override
  public Supplier<Builder<JsonNode>> supplier() {
    return f.iCollections()::listBuilder;
  }

}
