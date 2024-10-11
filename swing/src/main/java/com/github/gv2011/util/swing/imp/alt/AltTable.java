package com.github.gv2011.util.swing.imp.alt;

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

import static com.github.gv2011.util.ex.Exceptions.bug;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static com.github.gv2011.util.icol.ICollections.toISortedMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.Dimension;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.slf4j.Logger;

import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.ISortedMap;

public class AltTable extends JTable {

  private static final Logger LOG = getLogger(AltTable.class);
  private final String name;

  public AltTable(final String name, final TableModel model) {
    super(model, new AltTableColumnModel(model));
    this.name = name;
    resizeAndRepaint();
  }


  @Override
  public String toString() {
    return name;
  }



  @Override
  @Deprecated
  protected TableColumnModel createDefaultColumnModel() {
    throw bug();
  }

  @Override
  public boolean getAutoCreateColumnsFromModel() {
    return false;
  }

  @Override
  @Deprecated
  public void createDefaultColumnsFromModel() {
    throw bug();
  }

  @Override
  @Deprecated
  public void setAutoCreateColumnsFromModel(final boolean autoCreateColumnsFromModel) {
    throw bug();
  }


  @Override
  public AltTableColumnModel getColumnModel() {
    return (AltTableColumnModel) columnModel;
  }

  @Override
  public void setColumnModel(final TableColumnModel columnModel) {
    setAltColumnModel((AltTableColumnModel) columnModel);
  }

  public void setAltColumnModel(final AltTableColumnModel columnModel) {
    super.setColumnModel(columnModel);
  }

  @Override
  @Deprecated
  public void addColumn(final TableColumn aColumn) {
    throw bug();
  }

  @Override
  public Dimension getPreferredScrollableViewportSize() {
    return super.getPreferredScrollableViewportSize();
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(getWidth(), super.getPreferredSize().height);
  }

  @Override
  public Dimension getMinimumSize() {
    return new Dimension(getMinimumWidth(), super.getMinimumSize().height);
  }

  public int getMinimumWidth() {
    final AltTableColumnModel cm = (AltTableColumnModel) columnModel;
    final int count = cm.getColumnCount();
    int minTotal = 0;
    for(int i=0; i<count; i++) {
      final AltTableColumn column = cm.getColumn(i);
      minTotal += column.getMinWidth();
    }
    return minTotal;
  }



  @Override
  public void setSize(final Dimension size) {
    super.setSize(size);
  }


//  @Override
//  public void setMinimumSize(final Dimension minimumSize) {
//    throw new UnsupportedOperationException();
//  }



  @Override
  public void setSize(final int width, final int height) {
    super.setSize(width, height);
  }



  @Override
  public Dimension getSize(final Dimension rv) {
    final Dimension size = super.getSize();
    //verify(size, s->s.getWidth()>=getMinimumWidth());
    return size;
  }

  @Override
  public Dimension getSize() {
    final Dimension size = super.getSize();
    //verify(size, s->s.getWidth()>=getMinimumWidth());
    return size;
  }



  @Override
  public int getWidth() {
    final int width = super.getWidth();
    //verify(width, w->w>=getMinimumWidth());
    return width;
  }



  @SuppressWarnings("deprecation")
  @Override
  public void doLayout() {
    final Optional<AltTableColumn> resizingColumn = getResizingColumn();
    if (!resizingColumn.isPresent()) {
      LOG.trace("{}: doLayout (all columns)", this);
    } else {
      final AltTableColumn rc = resizingColumn.get();
      LOG.trace("{}: doLayout (column {})", this, rc);
    }
    resizeAll();
    layout();
  }


  private void resizeAll() {
    final AltTableColumnModel cm = (AltTableColumnModel) columnModel;
    final int count = cm.getColumnCount();
    if(count!=0) {
      final ISortedMap<Long, ISet<AltTableColumn>> byWeight =
        cm.stream()
        .collect(Collectors.groupingByConcurrent(AltTableColumn::getWeight, toISet()))
        .entrySet().stream()
        .collect(toISortedMap())
      ;
      int available = getWidth();
      long remainingWeight = cm.stream()
        .mapToLong(AltTableColumn::getWeight)
        .sum()
      ;
      int done = 0;
      for(final Entry<Long, ISet<AltTableColumn>> e: byWeight.entrySet()) {
        final long weight = e.getKey();
        for(final AltTableColumn c: e.getValue()) {
          final boolean last = done==count-1;
          final int newSize = last
            ? available
            : Math.max(
              (int) ((double)weight / (double)remainingWeight * (double)available),
              c.getMinWidth()
            )
          ;
          c.setWidthInternal(newSize);
          available -= newSize;
          remainingWeight -= weight;
          done++;
        }
      }
    }
  }

  private Optional<AltTableColumn> getResizingColumn() {
    return Optional.ofNullable(tableHeader)
      .flatMap(h->Optional.ofNullable((AltTableColumn)tableHeader.getResizingColumn()))
    ;
  }




  public void viewPortChanged(final JViewport viewport) {
    final int vpWidth = viewport.getWidth();
    final int width = getWidth();
    if(width!=vpWidth) {
      setSize(Math.max(vpWidth, getMinimumWidth()), getHeight());
    }
  }

  @Override
  public void columnMarginChanged(final ChangeEvent e) {
    if (isEditing() && !getCellEditor().stopCellEditing()) {
        getCellEditor().cancelCellEditing();
    }
    resizeAndRepaint();
  }

}
