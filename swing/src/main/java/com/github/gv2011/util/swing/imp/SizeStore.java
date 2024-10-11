package com.github.gv2011.util.swing.imp;

/*-
 * #%L
 * util-swing
 * %%
 * Copyright (C) 2018 - 2019 Vinz (https://github.com/gv2011)
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
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.slf4j.Logger;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.PropertyUtils;
import com.github.gv2011.util.PropertyUtils.SafeProperties;

public class SizeStore implements AutoCloseableNt{

  private static final Logger LOG = getLogger(SizeStore.class);

  private static final Path PATH = Paths.get("sizes.properties");
  private final SafeProperties sizes;

  public SizeStore() {
    sizes = PropertyUtils.readProperties(PATH);
  }

  private void registerSize(final String tableId, final TableColumn column) {
    final String id = tableId+"."+column.getIdentifier();
    sizes.tryGet(id).ifPresent(s->{
      column.setPreferredWidth(Integer.parseInt(s));
      LOG.debug("Set width of {} to {}", id, s);
    });
    column.addPropertyChangeListener(e->{
      if(e.getPropertyName().equals("width")) {
        final int width = column.getWidth();
        sizes.setProperty(id, Integer.toString(width));
        LOG.debug("Saved width of {}: {}", id, width);
      }
    });
  }

  @Override
  public void close() {
    sizes.store(PATH);
  }

  public void registerSizes(final JTable table) {
    final String id = table.getModel().getClass().getSimpleName();
    final TableColumnModel m = table.getColumnModel();
    for(int i=0; i<m.getColumnCount(); i++) {
      registerSize(id, m.getColumn(i));
    }
  }

  public void registerSizes(final String splitId, final JSplitPane splitPane) {
    sizes.tryGet(splitId).ifPresent(s->splitPane.setDividerLocation(Integer.parseInt(s)));
    splitPane.addPropertyChangeListener(e->{
      if(e.getPropertyName().equals("lastDividerLocation")) {
        final int location = splitPane.getDividerLocation();
        LOG.debug("{}: dividerLocation: {} (source:{})", splitId, location, e.getSource());
        if(location>0) {
          sizes.setProperty(splitId, Integer.toString(location));
        }
      }
    });
  }

  public void registerSize(final JFrame frame) {
    sizes.tryGet("main.width").ifPresent(w->{
      sizes.tryGet("main.height").ifPresent(h->{
        frame.setSize(Integer.parseInt(w), Integer.parseInt(h));
      });
    });
    frame.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(final ComponentEvent e) {
        final Dimension size = frame.getSize();
        sizes.setProperty("main.width", Integer.toString(size.width));
        sizes.setProperty("main.height", Integer.toString(size.height));
      }
    });
  }

}
