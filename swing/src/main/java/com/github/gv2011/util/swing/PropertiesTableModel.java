package com.github.gv2011.util.swing;

/*-
 * #%L
 * util-swing
 * %%
 * Copyright (C) 2018 Vinz (https://github.com/gv2011)
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

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.icol.ICollections.setOf;
import static com.github.gv2011.util.icol.ICollections.toISortedSet;

import java.lang.reflect.Method;
import java.util.Optional;

import javax.swing.table.AbstractTableModel;

import com.github.gv2011.util.XStream;
import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISet;

public final class PropertiesTableModel extends AbstractTableModel{

  private static final ISet<String> EXCLUDED = setOf("notify", "notifyAll", "wait");

  private final IList<String> properties;
  private final Optional<?> data;

  public PropertiesTableModel(final Optional<?> data) {
    this.data = data;
    properties = data
      .map(d->
        XStream.of(d.getClass().getMethods())
        .filter(m->m.getParameterCount()==0)
        .map(Method::getName)
        .filter(n->!EXCLUDED.contains(n))
        .collect(toISortedSet())
        .asList()
      )
      .orElseGet(ICollections::emptyList)
    ;
  }

  @Override
  public String getColumnName(final int column) {return column==0?"key":"value";}
  @Override
  public int getRowCount() {return properties.size();}
  @Override
  public int getColumnCount() {return 2;}
  @Override
  public Object getValueAt(final int row, final int col) {
    final String p = properties.get(row);
    if(col==0) return p;
    else return call(()->data.get().getClass().getMethod(p).invoke(data.get()).toString());
  }
}
