/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.github.gv2011.util.swing.alt;

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
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.bug;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.github.gv2011.util.legacy.LegacyCollectionUtils;

public final class AltTableColumnModel implements TableColumnModel,
                        PropertyChangeListener, ListSelectionListener, Serializable
{
//
// Instance Variables
//

    /** Array of TableColumn objects in this model */
    private final List<AltTableColumn> tableColumns;

    /** Model for keeping track of column selections */
    private ListSelectionModel selectionModel;

    /** Width margin between each column */
    private int columnMargin;

    /** List of TableColumnModelListener */
    private final EventListenerList listenerList = new EventListenerList();

    /** Change event (only one needed) */
    transient private ChangeEvent changeEvent = null;

    /** Column selection allowed in this column model */
    private boolean columnSelectionAllowed;

    /** A local cache of the combined width of all columns */
    private int totalColumnWidth;

    AltTableColumnModel(final TableModel tableModel) {
      tableColumns = new ArrayList<>();
      setSelectionModel(createSelectionModel());
      setColumnMargin(1);
      invalidateWidthCache();
      setColumnSelectionAllowed(false);

      final int count = tableModel.getColumnCount();
      for(int modelIndex = 0; modelIndex<count; modelIndex++) {
        final AltTableColumn newColumn = new AltTableColumn(
            tableModel.getColumnName(modelIndex), modelIndex, -1, AltTableColumn.DEFAULT_WEIGHT
        );
        addToList(newColumn);
        newColumn.addPropertyChangeListener(this);

        // Post columnAdded event notification
        fireColumnAdded(new TableColumnModelEvent(this, 0, getColumnCount() - 1));
      }
    }

    private AltTableColumn getFromList(final int index) {
      return tableColumns.get(index);
    }

    @Override
    public int getColumnCount() {
        return tableColumns.size();
    }

    @Override
    public Enumeration<TableColumn> getColumns() {
      return LegacyCollectionUtils.enumeration(tableColumns);
    }

    private void removeFromList(final AltTableColumn col) {
      final int index = col.getDisplayIndex();
      tableColumns.remove(index);
      col.setDisplayIndex(-1);
      for(int i=index; i<tableColumns.size(); i++) tableColumns.get(i).setDisplayIndex(i);
    }

    private void insertIntoList(final AltTableColumn column, final int index) {
      tableColumns.add(index, column);
      for(int i=index; i<tableColumns.size(); i++) tableColumns.get(i).setDisplayIndex(i);
    }

    private void addToList(final AltTableColumn column) {
      verifyEqual(column.getDisplayIndex(), -1);
      column.setDisplayIndex(tableColumns.size());
      tableColumns.add(column);
    }

//
// Modifying the model
//

    @Override
    @Deprecated
    public void addColumn(final TableColumn aColumn) {
      throw bug();
    }

    @Override
    @Deprecated
    public void removeColumn(final TableColumn column) {
      throw bug();
    }



    /**
     * Moves the column and heading at <code>columnIndex</code> to
     * <code>newIndex</code>.  The old column at <code>columnIndex</code>
     * will now be found at <code>newIndex</code>.  The column
     * that used to be at <code>newIndex</code> is shifted
     * left or right to make room.  This will not move any columns if
     * <code>columnIndex</code> equals <code>newIndex</code>.  This method
     * also posts a <code>columnMoved</code> event to its listeners.
     *
     * @param   columnIndex                     the index of column to be moved
     * @param   newIndex                        new index to move the column
     * @exception IllegalArgumentException      if <code>column</code> or
     *                                          <code>newIndex</code>
     *                                          are not in the valid range
     */
    @Override
    public void moveColumn(final int columnIndex, final int newIndex) {
        if ((columnIndex < 0) || (columnIndex >= getColumnCount()) ||
            (newIndex < 0) || (newIndex >= getColumnCount()))
            throw new IllegalArgumentException("moveColumn() - Index out of range");

        AltTableColumn aColumn;

        // If the column has not yet moved far enough to change positions
        // post the event anyway, the "draggedDistance" property of the
        // tableHeader will say how far the column has been dragged.
        // Here we are really trying to get the best out of an
        // API that could do with some rethinking. We preserve backward
        // compatibility by slightly bending the meaning of these methods.
        if (columnIndex == newIndex) {
            fireColumnMoved(new TableColumnModelEvent(this, columnIndex, newIndex));
            return;
        }
        aColumn = getFromList(columnIndex);

        removeFromList(aColumn);

        final boolean selected = selectionModel.isSelectedIndex(columnIndex);
        selectionModel.removeIndexInterval(columnIndex,columnIndex);

        insertIntoList(aColumn, newIndex);

        selectionModel.insertIndexInterval(newIndex, 1, true);
        if (selected) {
            selectionModel.addSelectionInterval(newIndex, newIndex);
        }
        else {
            selectionModel.removeSelectionInterval(newIndex, newIndex);
        }

        fireColumnMoved(new TableColumnModelEvent(this, columnIndex,
                                                               newIndex));
    }


    /**
     * Sets the column margin to <code>newMargin</code>.  This method
     * also posts a <code>columnMarginChanged</code> event to its
     * listeners.
     *
     * @param   newMargin               the new margin width, in pixels
     * @see     #getColumnMargin
     * @see     #getTotalColumnWidth
     */
    @Override
    public void setColumnMargin(final int newMargin) {
        if (newMargin != columnMargin) {
            columnMargin = newMargin;
            // Post columnMarginChanged event notification.
            fireColumnMarginChanged();
        }
    }


//
// Querying the model
//




    /**
     * Returns the index of the first column in the <code>tableColumns</code>
     * array whose identifier is equal to <code>identifier</code>,
     * when compared using <code>equals</code>.
     *
     * @param           identifier              the identifier object
     * @return          the index of the first column in the
     *                  <code>tableColumns</code> array whose identifier
     *                  is equal to <code>identifier</code>
     * @exception       IllegalArgumentException  if <code>identifier</code>
     *                          is <code>null</code>, or if no
     *                          <code>TableColumn</code> has this
     *                          <code>identifier</code>
     * @see             #getColumn
     */
    @Override
    public int getColumnIndex(final Object identifier) {
        if (identifier == null) {
            throw new IllegalArgumentException("Identifier is null");
        }

        final Enumeration<?> enumeration = getColumns();
        TableColumn aColumn;
        int index = 0;

        while (enumeration.hasMoreElements()) {
            aColumn = (TableColumn)enumeration.nextElement();
            // Compare them this way in case the column's identifier is null.
            if (identifier.equals(aColumn.getIdentifier()))
                return index;
            index++;
        }
        throw new IllegalArgumentException("Identifier not found");
    }

    /**
     * Returns the <code>TableColumn</code> object for the column
     * at <code>columnIndex</code>.
     *
     * @param   columnIndex     the index of the column desired
     * @return  the <code>TableColumn</code> object for the column
     *                          at <code>columnIndex</code>
     */
    @Override
    public AltTableColumn getColumn(final int columnIndex) {
      return getFromList(columnIndex);
    }

    /**
     * Returns the width margin for <code>TableColumn</code>.
     * The default <code>columnMargin</code> is 1.
     *
     * @return  the maximum width for the <code>TableColumn</code>
     * @see     #setColumnMargin
     */
    @Override
    public int getColumnMargin() {
        return columnMargin;
    }

    /**
     * Returns the index of the column that lies at position <code>x</code>,
     * or -1 if no column covers this point.
     *
     * In keeping with Swing's separable model architecture, a
     * TableColumnModel does not know how the table columns actually appear on
     * screen.  The visual presentation of the columns is the responsibility
     * of the view/controller object using this model (typically JTable).  The
     * view/controller need not display the columns sequentially from left to
     * right.  For example, columns could be displayed from right to left to
     * accommodate a locale preference or some columns might be hidden at the
     * request of the user.  Because the model does not know how the columns
     * are laid out on screen, the given <code>xPosition</code> should not be
     * considered to be a coordinate in 2D graphics space.  Instead, it should
     * be considered to be a width from the start of the first column in the
     * model.  If the column index for a given X coordinate in 2D space is
     * required, <code>JTable.columnAtPoint</code> can be used instead.
     *
     * @param  x  the horizontal location of interest
     * @return  the index of the column or -1 if no column is found
     * @see javax.swing.JTable#columnAtPoint
     */
    @Override
    public int getColumnIndexAtX(int x) {
        if (x < 0) {
            return -1;
        }
        final int cc = getColumnCount();
        for(int column = 0; column < cc; column++) {
            x = x - getColumn(column).getWidth();
            if (x < 0) {
                return column;
            }
        }
        return -1;
    }

    /**
     * Returns the total combined width of all columns.
     * @return the <code>totalColumnWidth</code> property
     */
    @Override
    public int getTotalColumnWidth() {
        if (totalColumnWidth == -1) {
            recalcWidthCache();
        }
        return totalColumnWidth;
    }

//
// Selection model
//

    /**
     *  Sets the selection model for this <code>TableColumnModel</code>
     *  to <code>newModel</code>
     *  and registers for listener notifications from the new selection
     *  model.  If <code>newModel</code> is <code>null</code>,
     *  an exception is thrown.
     *
     * @param   newModel        the new selection model
     * @exception IllegalArgumentException      if <code>newModel</code>
     *                                          is <code>null</code>
     * @see     #getSelectionModel
     */
    @Override
    public void setSelectionModel(final ListSelectionModel newModel) {
        if (newModel == null) {
            throw new IllegalArgumentException("Cannot set a null SelectionModel");
        }

        final ListSelectionModel oldModel = selectionModel;

        if (newModel != oldModel) {
            if (oldModel != null) {
                oldModel.removeListSelectionListener(this);
            }

            selectionModel= newModel;
            newModel.addListSelectionListener(this);
        }
    }

    /**
     * Returns the <code>ListSelectionModel</code> that is used to
     * maintain column selection state.
     *
     * @return  the object that provides column selection state.  Or
     *          <code>null</code> if row selection is not allowed.
     * @see     #setSelectionModel
     */
    @Override
    public ListSelectionModel getSelectionModel() {
        return selectionModel;
    }

    // implements javax.swing.table.TableColumnModel
    /**
     * Sets whether column selection is allowed.  The default is false.
     * @param  flag true if column selection will be allowed, false otherwise
     */
    @Override
    public void setColumnSelectionAllowed(final boolean flag) {
        columnSelectionAllowed = flag;
    }

    // implements javax.swing.table.TableColumnModel
    /**
     * Returns true if column selection is allowed, otherwise false.
     * The default is false.
     * @return the <code>columnSelectionAllowed</code> property
     */
    @Override
    public boolean getColumnSelectionAllowed() {
        return columnSelectionAllowed;
    }

    // implements javax.swing.table.TableColumnModel
    /**
     * Returns an array of selected columns.  If <code>selectionModel</code>
     * is <code>null</code>, returns an empty array.
     * @return an array of selected columns or an empty array if nothing
     *                  is selected or the <code>selectionModel</code> is
     *                  <code>null</code>
     */
    @Override
    public int[] getSelectedColumns() {
        if (selectionModel != null) {
            final int iMin = selectionModel.getMinSelectionIndex();
            final int iMax = selectionModel.getMaxSelectionIndex();

            if ((iMin == -1) || (iMax == -1)) {
                return new int[0];
            }

            final int[] rvTmp = new int[1+ (iMax - iMin)];
            int n = 0;
            for(int i = iMin; i <= iMax; i++) {
                if (selectionModel.isSelectedIndex(i)) {
                    rvTmp[n++] = i;
                }
            }
            final int[] rv = new int[n];
            System.arraycopy(rvTmp, 0, rv, 0, n);
            return rv;
        }
        return  new int[0];
    }

    // implements javax.swing.table.TableColumnModel
    /**
     * Returns the number of columns selected.
     * @return the number of columns selected
     */
    @Override
    public int getSelectedColumnCount() {
        if (selectionModel != null) {
            final int iMin = selectionModel.getMinSelectionIndex();
            final int iMax = selectionModel.getMaxSelectionIndex();
            int count = 0;

            for(int i = iMin; i <= iMax; i++) {
                if (selectionModel.isSelectedIndex(i)) {
                    count++;
                }
            }
            return count;
        }
        return 0;
    }

//
// Listener Support Methods
//

    // implements javax.swing.table.TableColumnModel
    /**
     * Adds a listener for table column model events.
     * @param x  a <code>TableColumnModelListener</code> object
     */
    @Override
    public void addColumnModelListener(final TableColumnModelListener x) {
        listenerList.add(TableColumnModelListener.class, x);
    }

    // implements javax.swing.table.TableColumnModel
    /**
     * Removes a listener for table column model events.
     * @param x  a <code>TableColumnModelListener</code> object
     */
    @Override
    public void removeColumnModelListener(final TableColumnModelListener x) {
        listenerList.remove(TableColumnModelListener.class, x);
    }

    /**
     * Returns an array of all the column model listeners
     * registered on this model.
     *
     * @return all of this default table column model's <code>ColumnModelListener</code>s
     *         or an empty
     *         array if no column model listeners are currently registered
     *
     * @see #addColumnModelListener
     * @see #removeColumnModelListener
     *
     * @since 1.4
     */
    public TableColumnModelListener[] getColumnModelListeners() {
        return listenerList.getListeners(TableColumnModelListener.class);
    }

//
//   Event firing methods
//

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @param e  the event received
     * @see EventListenerList
     */
    private void fireColumnAdded(final TableColumnModelEvent e) {
        // Guaranteed to return a non-null array
        final Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TableColumnModelListener.class) {
                // Lazily create the event:
                // if (e == null)
                //  e = new ChangeEvent(this);
                ((TableColumnModelListener)listeners[i+1]).
                    columnAdded(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @param  e the event received
     * @see EventListenerList
     */
    private void fireColumnMoved(final TableColumnModelEvent e) {
        // Guaranteed to return a non-null array
        final Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TableColumnModelListener.class) {
                // Lazily create the event:
                // if (e == null)
                //  e = new ChangeEvent(this);
                ((TableColumnModelListener)listeners[i+1]).
                    columnMoved(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @param e the event received
     * @see EventListenerList
     */
    private void fireColumnSelectionChanged(final ListSelectionEvent e) {
        // Guaranteed to return a non-null array
        final Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TableColumnModelListener.class) {
                // Lazily create the event:
                // if (e == null)
                //  e = new ChangeEvent(this);
                ((TableColumnModelListener)listeners[i+1]).
                    columnSelectionChanged(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    private void fireColumnMarginChanged() {
        // Guaranteed to return a non-null array
        final Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TableColumnModelListener.class) {
                // Lazily create the event:
                if (changeEvent == null)
                    changeEvent = new ChangeEvent(this);
                ((TableColumnModelListener)listeners[i+1]).
                    columnMarginChanged(changeEvent);
            }
        }
    }

    /**
     * Returns an array of all the objects currently registered
     * as <code><em>Foo</em>Listener</code>s
     * upon this model.
     * <code><em>Foo</em>Listener</code>s are registered using the
     * <code>add<em>Foo</em>Listener</code> method.
     *
     * <p>
     *
     * You can specify the <code>listenerType</code> argument
     * with a class literal,
     * such as
     * <code><em>Foo</em>Listener.class</code>.
     * For example, you can query a
     * <code>DefaultTableColumnModel</code> <code>m</code>
     * for its column model listeners with the following code:
     *
     * <pre>ColumnModelListener[] cmls = (ColumnModelListener[])(m.getListeners(ColumnModelListener.class));</pre>
     *
     * If no such listeners exist, this method returns an empty array.
     *
     * @param listenerType the type of listeners requested; this parameter
     *          should specify an interface that descends from
     *          <code>java.util.EventListener</code>
     * @return an array of all objects registered as
     *          <code><em>Foo</em>Listener</code>s on this model,
     *          or an empty array if no such
     *          listeners have been added
     * @exception ClassCastException if <code>listenerType</code>
     *          doesn't specify a class or interface that implements
     *          <code>java.util.EventListener</code>
     *
     * @see #getColumnModelListeners
     * @since 1.3
     */
    public <T extends EventListener> T[] getListeners(final Class<T> listenerType) {
        return listenerList.getListeners(listenerType);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        final String name = evt.getPropertyName();

        if (name == "width") {
            invalidateWidthCache();
            // This is a misnomer, we're using this method
            // simply to cause a relayout.
            fireColumnMarginChanged();
        }
        else if(name.equals("preferredWidth")) {
          recalculateWeights((AltTableColumn) evt.getSource());
          // This is a misnomer, we're using this method
          // simply to cause a relayout.
          fireColumnMarginChanged();
        }
    }

    private void recalculateWeights(final AltTableColumn column) {
      final int count = getColumnCount();
      if(!column.equals(getColumn(count-1))) {
        final int oldWidth = column.getWidth();
        final int rawNewWidth = column.getRequestedWidth();
        verify(rawNewWidth!=oldWidth);
        final int idx = column.getDisplayIndex();
        verify(getColumn(idx).equals(column));
        verify(idx < count-1);
        //ensure limit to the right:
        final int widthRigth = IntStream.range(idx+1, count).map(i->getColumn(i).getWidth()).sum();
        final int minRigth = IntStream.range(idx+1, count).map(i->getColumn(i).getMinWidth()).sum();
        final int max = widthRigth - minRigth;
        verify(max>=0);
        final int newWidth = Math.min(rawNewWidth, oldWidth+max);
        final int move = newWidth - oldWidth; //positive to the right
        //if moving to the left:
        //  * columns to the right grow, those to the left remain unchanged
        //  * weight to the left stays unchanged, weight of column decreases, weight right increases by same amount
        //if moving to the right: columns to the right decrease, those to the left remain unchanged
        final long colWeightOld = column.getWeight();
        final long rightWeightOld = IntStream.range(idx+1, count)
          .mapToLong(i->getColumn(i).getWeight())
          .sum()
        ;
        final long weightDiff = (long) (((double)move/(double)oldWidth) * (double)colWeightOld);
        final long colWeightNew = colWeightOld + weightDiff;
        column.setWeight(colWeightNew);
        long rightWeightAvailable = rightWeightOld - weightDiff;
        long rightWeightBase = rightWeightOld;
        double factor = (double)rightWeightAvailable / (double)rightWeightBase;
        for(int i=idx+1; i<count-1; i++) {
          final AltTableColumn c = getColumn(i);
          final long oldWeight = c.getWeight();
          final long newWeight = (long)((double)oldWeight*factor);
          c.setWeight(newWeight);
          rightWeightAvailable -= newWeight;
          rightWeightBase -= oldWeight;
          factor = (double)rightWeightAvailable / (double)rightWeightBase;
        }
        getColumn(count-1).setWeight(rightWeightAvailable);
        verifyEqual(
          stream().mapToLong(AltTableColumn::getWeight).sum(),
          count*AltTableColumn.DEFAULT_WEIGHT
        );
      }
    }

//
// Implementing ListSelectionListener interface
//

    // implements javax.swing.event.ListSelectionListener
    /**
     * A <code>ListSelectionListener</code> that forwards
     * <code>ListSelectionEvents</code> when there is a column
     * selection change.
     *
     * @param e  the change event
     */
    @Override
    public void valueChanged(final ListSelectionEvent e) {
        fireColumnSelectionChanged(e);
    }

//
// Protected Methods
//

    /**
     * Creates a new default list selection model.
     */
    private ListSelectionModel createSelectionModel() {
        return new DefaultListSelectionModel();
    }

    /**
     * Recalculates the total combined width of all columns.  Updates the
     * <code>totalColumnWidth</code> property.
     */
    private void recalcWidthCache() {
        final Enumeration<?> enumeration = getColumns();
        totalColumnWidth = 0;
        while (enumeration.hasMoreElements()) {
            totalColumnWidth += ((TableColumn)enumeration.nextElement()).getWidth();
        }
    }

    private void invalidateWidthCache() {
        totalColumnWidth = -1;
    }

    public Stream<AltTableColumn> stream() {
      return IntStream.range(0, getColumnCount()).parallel().mapToObj(this::getColumn);
    }

}
