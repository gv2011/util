package com.github.gv2011.util.swing.imp.builder;

import static com.github.gv2011.util.EnumUtils.values;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;

import com.github.gv2011.util.swing.Gui;

final class GuiImp implements Gui {

  private static final Logger LOG = getLogger(GuiImp.class);

  static final short ZERO = 0;

  private final Object lock = new Object();

  private final HTabImp right;
  private final VTabImp bottom;
  private final JFrame frame;
  private final JPanel contentPane;

  private boolean closed;


  GuiImp(final HTabImp right, final VTabImp bottom, final Dimension initialSize){
    this.right = right;
    this.bottom = bottom;
    frame = new JFrame();
    frame.addWindowListener(new WindowAdapter(){
      @Override
      public void windowClosing(final WindowEvent e) {
        close();
      }
    });
    contentPane = new JPanel(){
      @Override
      public void setBounds(final int x, final int y, final int width, final int height) {
        super.setBounds(x, y, width, height);
      }
      @Override
      public String toString() {
        return getName();
      }
    };
    {
      contentPane.setOpaque(true);
      contentPane.setName("cp");
      contentPane.setBackground(Color.cyan);
      contentPane.setPreferredSize(null);
      right.blocks().forEachOrdered(c->c.addTo(contentPane));
      contentPane.setLayout(new GvLayout(this));
      frame.setContentPane(contentPane);
      verifyEqual(frame.getRootPane().getContentPane(), contentPane);
    }
    frame.setSize(initialSize);
    frame.setVisible(true);
  }



  @Override
  public void setSize(final int width, final int height) {
    frame.setSize(new Dimension(width, height));
  }



  @Override
  public void close() {
    synchronized(lock){
      if(!closed){
        closed = true;
        lock.notifyAll();
        SwingUtilities.invokeLater(frame::dispose);
      }
    }
  }

  @Override
  public void waitUntilClosed() {
    synchronized(lock){
      while(!closed){call(()->lock.wait());}
    }
  }

  JFrame frame() {
    return frame;
  }

  void layout(final Dimension size) {
    values(Orientation.class).forEach(o->getHighest(o).reset());
    values(Orientation.class).forEach(o->{
      final TabImp<?> tab = getHighest(o);
      final short dimSize = shrt(o.size(size));
//      tab.flexibility();
//      final double factor = tab.flexibility().getFactor(dimSize);
//      LOG.debug("{} layout with size {} and factor {}.", o, dimSize, factor);
      final Elasticity elasticity = tab.elasticity();
      LOG.debug("{} elacitity: {}", o, elasticity);
      tab.setPosition(elasticity.limit(dimSize));
    });
  }

  private TabImp<?> getHighest(final Orientation o){
    return Stream.of(right, bottom).filter(t->t.orientation().equals(o)).findAny().orElseThrow();
  }

  static final short shrt(final int i){
    verify(i>=Short.MIN_VALUE && i<= Short.MAX_VALUE, ()->""+i);
    return (short)i;
  }

  static final short shrt(final double d){
    verify(d, i->i>=Short.MIN_VALUE && i<= Short.MAX_VALUE);
    return (short)d;
  }

  JPanel contentPane() {
    return this.contentPane;
  }


}
