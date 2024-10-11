package com.github.gv2011.util.swing.imp;

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

import java.awt.Dimension;
import java.util.function.Function;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import com.github.gv2011.util.swing.imp.alt.AltTable;

public class TableFactory<E> {

  private final Function<E,TableModel> modelFactory;
  @SuppressWarnings("unused") //TODO
  private final SizeStore sizeStore;

  public TableFactory(final SizeStore sizeStore, final Function<E, TableModel> modelFactory) {
    this.sizeStore = sizeStore;
    this.modelFactory = modelFactory;
  }

  public JScrollPane createTableView(final E obj) {
    final TableModel model = modelFactory.apply(obj);
    final AltTable table = new AltTable("table",model);
    table.setMinimumSize(new Dimension(model.getColumnCount()*15, 15));
    //table.setFillsViewportHeight(true);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    //sizeStore.registerSizes(table);
    final JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.getViewport().addChangeListener(e->table.viewPortChanged(scrollPane.getViewport()));
    return scrollPane;
  }


}
