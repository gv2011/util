package com.github.gv2011.util.beans.cglib;

import java.math.BigDecimal;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.tstr.TypedString;

/*-
 * #%L
 * util-beans-cglib
 * %%
 * Copyright (C) 2017 - 2018 Vinz (https://github.com/gv2011)
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
public final class PolymorphicTestModel {

  public static interface Subtype extends TypedString<Subtype>{}

  public static abstract class Animal implements Bean{
    public abstract Subtype type();
    public abstract Opt<? extends Animal> bestFriend();
    public abstract IList<? extends Animal> contacts();
    public Integer count(){
      return bestFriend()
        .map(b->contacts().contains(b) ? contacts().size() : contacts().size()+1)
        .orElse(contacts().size())
      ;
    }
  }

  public static abstract class Elephant extends Animal{
    public abstract BigDecimal volume();
    @Override
    public abstract Opt<Shark> bestFriend();

  }

  public static abstract class Shark extends Animal{
    public abstract BigDecimal volume();
    @Override
    public abstract IList<Elephant> contacts();
  }
}
