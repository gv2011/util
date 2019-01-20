package com.github.gv2011.util.cache;

/*-
 * #%L
 * util-test
 * %%
 * Copyright (C) 2016 - 2019 Vinz (https://github.com/gv2011)
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
import static com.github.gv2011.util.ex.Exceptions.notYetImplemented;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static org.junit.Assert.*;

import java.util.function.Function;

import org.junit.Ignore;
import org.junit.Test;

import com.github.gv2011.util.icol.ISet;

public class VariableTest{

  private final Variable<ISet<String>> names = Variables.createVariable(this::calculateAllNames);

  private final ISet<Function<Invalidator,ISet<String>>> suppliers = notYetImplemented();

  public final ISet<String> getAllNames(final Invalidator invalidator){
    return names.get(invalidator);
  }

  public final ISet<String> calculateAllNames(final Invalidator invalidator){
    return suppliers.stream().flatMap(s->s.apply(invalidator).stream()).collect(toISet());
  }


  @Test
  @Ignore //TODO wip
  public void test() {
    fail("Not yet implemented");
  }

}
