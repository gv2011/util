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
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.bug;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public final class AltTableColumn extends TableColumn{

  static final long DEFAULT_WEIGHT = 1000;

  private static final int MIN_WIDTH = 30;
  private final String name;
  private long weight;
  private int displayIndex;

  AltTableColumn(final String name, final int modelIndex, final int displayIndex, final long weight) {
    super(modelIndex, MIN_WIDTH, null, null);
    this.displayIndex = displayIndex;
    this.weight = weight;
    minWidth = MIN_WIDTH;
    this.name = name;
    setIdentifier(name);
  }

  @Override
  public void setModelIndex(final int modelIndex) {
    super.setModelIndex(modelIndex);
  }

  void setDisplayIndex(final int displayIndex) {
    this.displayIndex = displayIndex;
  }

  int getDisplayIndex() {
    return displayIndex;
  }

  @Override
  public void setIdentifier(final Object identifier) {
    super.setIdentifier(identifier);
  }

  @Override
  @Deprecated
  public void setHeaderValue(final Object headerValue) {
    throw bug();
  }

  @Override
  public String getHeaderValue() {
    return name;
  }

  @Override
  public void setHeaderRenderer(final TableCellRenderer headerRenderer) {
    super.setHeaderRenderer(headerRenderer);
  }

  @Override
  public void setCellRenderer(final TableCellRenderer cellRenderer) {
    super.setCellRenderer(cellRenderer);
  }

  @Override
  public void setCellEditor(final TableCellEditor cellEditor) {
    super.setCellEditor(cellEditor);
  }

  @Override
  public int getWidth() {
    final int result = super.getWidth();
    verify(result>=MIN_WIDTH);
    return result;
  }

  @Override
  public void setWidth(final int width) {
    super.setPreferredWidth(Math.max(width, MIN_WIDTH));
  }

  int getRequestedWidth() {
    return super.getPreferredWidth();
  }

  @Override
  @Deprecated
  public void setPreferredWidth(final int preferredWidth) {
    throw bug();
  }

  void setWidthInternal(final int width) {
    super.setWidth(width);
  }

  void setWeight(final long weight) {
    verify(weight>0);
    this.weight = weight;
  }

  long getWeight() {
    return weight;
  }


  @Override
  @Deprecated
  public int getPreferredWidth() {
    return MIN_WIDTH;
  }

  @Override
  public int getMinWidth() {
    verify(super.getMinWidth(), w->w==MIN_WIDTH);
    return MIN_WIDTH;
  }

  @Override
  @Deprecated
  public void setMinWidth(final int minWidth) {
    throw bug();
  }

  @Override
  @Deprecated
  public void setMaxWidth(final int maxWidth) {
    throw bug();
  }

  @Override
  @Deprecated
  public void setResizable(final boolean isResizable) {
    throw bug();
  }

  @Override
  @Deprecated
  public void sizeWidthToFit() {
    throw bug();
  }

  @Override
  @Deprecated
  public void disableResizedPosting() {
    throw bug();
  }

  @Override
  @Deprecated
  public void enableResizedPosting() {
    throw bug();
  }

  @Override
  protected TableCellRenderer createDefaultHeaderRenderer() {
    return super.createDefaultHeaderRenderer();
  }

  @Override
  public String toString() {
    return name.isEmpty() ? Integer.toString(getModelIndex()) : name;
  }

}
