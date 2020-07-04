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
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.ex.Exceptions.call;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.slf4j.Logger;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.swing.log.LogPane;

public final class Explorer<E> implements AutoCloseableNt{

  static {SwingSetup.setup();}

  private static final Logger LOG = getLogger(Explorer.class);

  private final TreeNode<E> t;
  private final Object lock = new Object();

  private final Constant<JFrame> frame = Constants.cachedConstant(this::createFrame);
  private boolean created;
  private boolean visible;
  private final Runnable closeCallback;
  private final SizeStore sizeStore;
  private final TableFactory<E> tableFactory;

  public Explorer(
    final TreeNode<E> t,
    final Runnable closeCallback,
    final SizeStore sizeStore,
    final Function<E, TableModel> modelFactory
  ) {
    this.t = t;
    this.closeCallback = closeCallback;
    this.sizeStore = sizeStore;
    tableFactory = new TableFactory<>(sizeStore, modelFactory);
  }

  private final class ExplorerPanel extends JPanel implements AutoCloseableNt{

    private final JTree jTree;
    final JSplitPane upperView;
    private final LogPane logPane;

    private ExplorerPanel() {
      super(new GridLayout(1, 0));
      final DefaultMutableTreeNode top = createNode(t);

      {
        final JScrollPane treeView;
        {
          jTree = new JTree(top);
          jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
          jTree.addTreeSelectionListener(this::handleTreeSelectionEvent);
          treeView = new JScrollPane(jTree);
        }
//        {
//          contentPane = new TableFactory<>(sizeStore, modelFactory).createTableView("content");
//        }
        upperView = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        upperView.setLeftComponent(treeView);
        upperView.setRightComponent(new JPanel());
        sizeStore.registerSizes("upperSplit", upperView);
      }

      logPane = new LogPane(sizeStore);

      final JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
      mainSplit.setTopComponent(upperView);
      mainSplit.setBottomComponent(logPane);
      sizeStore.registerSizes("mainSplit", mainSplit);

      add(mainSplit);
    }

    @SuppressWarnings("unused")
    private void addSizeLogger(final Component component) {
      if(LOG.isDebugEnabled()) {
        component.addComponentListener(new ComponentAdapter() {
          @Override
          public void componentResized(final ComponentEvent evt) {
            final Rectangle bounds = component.getBounds();
            LOG.debug(
              "{} resized to width {} and height {}.", component.getName(), bounds.getWidth(), bounds.getHeight()
            );
          }}
        );
      }
    }

    private void handleTreeSelectionEvent(final TreeSelectionEvent evt) {
      final DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
      @SuppressWarnings("unchecked")
      final TreeNode<E> treeNode = (TreeNode<E>) notNull(node.getUserObject());
      display(treeNode);
    }

    private void display(final TreeNode<E> treeNode) {
      upperView.setRightComponent(tableFactory.createTableView(treeNode.payload().get()));
    }

    @Override
    public void close() {
      logPane.close();
    }
  }


  private DefaultMutableTreeNode createNode(final TreeNode<E> t) {
    final DefaultMutableTreeNode top = new DefaultMutableTreeNode(t);
    for(final TreeNode<E> child: t) {
      top.add(createNode(child));
    }
    return top;
  }

  private JFrame createFrame() {
    final AtomicReference<JFrame> ref = new AtomicReference<>();
    call(()->
      SwingUtilities.invokeAndWait(()->{
        final ExplorerPanel explorerPanel = new ExplorerPanel();
        final JFrame frame = new JFrame(Explorer.class.getSimpleName()) {
          @Override
          public void dispose() {
            explorerPanel.close();
            super.dispose();
          }
        };
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
          @Override
          public void windowClosing(final WindowEvent evt) {
            closeCallback.run();
          }}
        );
        frame.add(explorerPanel);
        sizeStore.registerSize(frame);
        //frame.pack();
        ref.set(frame);
      })
    );
    return notNull(ref.get());
  }

  public void setVisible(boolean visible) {
    synchronized(lock) {
      if(visible && !this.visible) {
        final JFrame frame = this.frame.get();
        created = true;
        call(()->SwingUtilities.invokeAndWait(()->{frame.setVisible(true);}));
        visible = true;
      }
      else if(!visible && this.visible) {
        final JFrame frame = this.frame.get();
        created = true;
        call(()->SwingUtilities.invokeAndWait(()->{frame.setVisible(false);}));
        visible = false;
      }
    }
  }

  @Override
  public void close() {
    synchronized(lock) {
      if(created) {
        frame.get().dispose();
      }
    }
  }

  public void disable() {
  }
}
