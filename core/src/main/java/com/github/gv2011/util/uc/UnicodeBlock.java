package com.github.gv2011.util.uc;

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
import static com.github.gv2011.util.icol.ICollections.sortedMapBuilder;

import java.util.stream.IntStream;

import com.github.gv2011.util.icol.ISortedMap;
import com.github.gv2011.util.icol.ISortedMap.Builder;
import com.github.gv2011.util.icol.Opt;

public final class UnicodeBlock {

  public static final ISortedMap<Integer,UnicodeBlock> BLOCKS;

  static{
    final Builder<Integer,UnicodeBlock> b = sortedMapBuilder();
    int index = 0;
    int startBefore = 0;
    Opt<Character.UnicodeBlock> blockBefore = Opt.ofNullable(Character.UnicodeBlock.of(0));
    for(int cp=1; cp<=Character.MAX_CODE_POINT; cp++){
      final Opt<Character.UnicodeBlock> block = Opt.ofNullable(Character.UnicodeBlock.of(cp));
      if(!block.equals(blockBefore)){
        b.put(startBefore, new UnicodeBlock(startBefore, cp-1, index++));
        blockBefore = block;
        startBefore = cp;
      }
    }
    b.put(startBefore, new UnicodeBlock(startBefore, Character.MAX_CODE_POINT, index++));
    BLOCKS = b.build();
  }

  private final Opt<Character.UnicodeBlock> block;
  private final String name;
  private final int index;
  private final int size;
  private final int offset;
  final boolean allDefined;
  final boolean surrogate;

  private UnicodeBlock(final int first, final int last, final int index){
    block = Opt.ofNullable(Character.UnicodeBlock.of(first));
    name = block.map(Character.UnicodeBlock::toString).orElseGet(()->"_"+Integer.toHexString(first));
    this.index = index;
    offset = first;
    size = last-first+1;
    allDefined = IntStream.range(first, last+1).allMatch(Character::isDefined);
    surrogate = first>Character.MAX_VALUE ? false : Character.isSurrogate((char) first);
  }

  @Override
  public String toString() {
    return name;
  }

  public int size(){
    return size;
  }

  public int index(){
    return index;
  }

  public int offset(){
    return offset;
  }

  public boolean defined(){
    return block.isPresent();
  }
}
