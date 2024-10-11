package com.github.gv2011.util.swing;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class SwingBuilderExample {

  public static void main(final String[] args){
    final JTextField tf = new JTextField();
    tf.setBackground(Color.BLUE);
    tf.setPreferredSize(new Dimension(100, tf.getMinimumSize().height));
    tf.setMaximumSize(new Dimension(1000, 20));
    final JPanel jp = new JPanel();
    jp.setBackground(Color.GRAY);
    final Gui gui = SwingUtils.guiBuilder()
      .add(tf, false).add(jp, true)
      .addSoftGap(false)
      .build()
    ;
    gui.waitUntilClosed();
  }

}
