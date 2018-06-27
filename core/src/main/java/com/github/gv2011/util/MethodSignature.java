package com.github.gv2011.util;

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

import static com.github.gv2011.util.Comparison.compareByAttribute;
import static com.github.gv2011.util.Comparison.listComparator;
import static com.github.gv2011.util.icol.ICollections.asList;

import java.lang.reflect.Method;
import java.util.Comparator;

import com.github.gv2011.util.icol.IList;

public final class MethodSignature implements Comparable<MethodSignature>{

  private static Comparator<IList<Class<?>>> PCOMP = listComparator(compareByAttribute(Class::getName));

  private final String name;
  private final IList<Class<?>> parameters;

  public MethodSignature(final Method m) {
    name = m.getName();
    parameters = asList(m.getParameterTypes());
  }

  public String name() {
    return name;
  }

  public IList<Class<?>> parameters(){
    return parameters;
  }

  @Override
  public int hashCode() {
    return Equal.hashCode(MethodSignature.class, name, parameters);
  }

  @Override
  public boolean equals(final Object obj) {
    return Equal.equal(this, obj, MethodSignature.class);
  }

  @Override
  public String toString() {
    return name+parameters;
  }

  @Override
  public int compareTo(final MethodSignature o) {
    int result = name.compareTo(o.name);
    if(result==0)  result = PCOMP.compare(parameters, o.parameters);
    return result;
  }

}
