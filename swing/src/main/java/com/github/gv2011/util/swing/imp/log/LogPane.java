package com.github.gv2011.util.swing.imp.log;

import static com.github.gv2011.util.icol.ICollections.listOf;

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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.BoundedRangeModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.swing.imp.SizeStore;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class LogPane extends JScrollPane implements AutoCloseableNt{

  private final JTable table;
  private final LogPaneTableModel model;
  private final Object lock = new Object();
  private final List<ILoggingEvent> events = new ArrayList<>();
  private final List<ILoggingEvent> newEvents = new ArrayList<>();
  private boolean updatePending;
  private boolean closed;
  private final AutoCloseableNt subscription;

  public LogPane(final SizeStore sizeStore) {
    model = new LogPaneTableModel();
    table = new JTable(model);
    sizeStore.registerSizes(table);
    setViewportView(table);
    subscription = SwingAppender.subscribe(this::receive);
  }

  private void receive(final ILoggingEvent e) {
    synchronized(lock) {
      if(!closed) {
        newEvents.add(e);
        if(!updatePending) {
          updatePending = true;
          SwingUtilities.invokeLater(this::update);
        }
      }
    }
  }

  private void update() {
    int count;
    synchronized(lock) {
      updatePending = false;
      if(!closed && !newEvents.isEmpty()) {
        count = newEvents.size();
        events.addAll(newEvents);
        newEvents.clear();
      }
      else count = 0;
    }
    if(count>0) {
      model.fireTableRowsInserted(events.size()-count, events.size()-1);
      SwingUtilities.invokeLater(()->{
        final BoundedRangeModel m = getVerticalScrollBar().getModel();
        m.setValue(m.getMaximum()-m.getExtent());
      });
    }
  }

  @Override
  public void close() {
    subscription.close();
    synchronized(lock) {
      closed = true;
    }
  }

  private class LogPaneTableModel extends AbstractTableModel {

    private final IList<Column> columns = columns();

    @Override
    public int getColumnCount() {
      return columns.size();
    }

    private IList<Column> columns() {
      return listOf(
        new Column("Time", e->Instant.ofEpochMilli(e.getTimeStamp())),
        new Column("Level", ILoggingEvent::getLevel),
        new Column("Logger", ILoggingEvent::getLoggerName),
        new Column("Message", ILoggingEvent::getFormattedMessage)
      );
    }

    @Override
    public String getColumnName(final int col) {
      return columns.get(col).name;
    }

    @Override
    public int getRowCount() {
      return events.size();
    }

    @Override
    public Object getValueAt(final int row, final int col) {
      return columns.get(col).getter.apply(events.get(row));
    }
  }

  private static class Column {
    private final Function<ILoggingEvent,Object> getter;
    private final String name;
    private Column(final String name, final Function<ILoggingEvent, Object> getter) {
      this.name = name;
      this.getter = getter;
    }
  }


}
