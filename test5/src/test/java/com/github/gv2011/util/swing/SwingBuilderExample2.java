package com.github.gv2011.util.swing;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class SwingBuilderExample2 {

  public static void main(final String[] args){
    final GuiBuilder builder = SwingUtils.guiBuilder();

    final int margin = 5;
    builder.addVerticalHardGap(margin, true);

    final HTab h1 = builder.add(label("Given name"), false).position().hTab();
    final HTab h2 = builder.add(textField(),    false).position().hTab();
    builder.addSoftGap(true);

    final Gui gui = builder
      .add(label("Last name"), h1).add(textField(), h2).addSoftGap(true)
      .addSoftGap(false)
      .setSize(600,100)
      .build()
    ;

//    gui.setSize(600,100);
    gui.waitUntilClosed();
  }

  private static JTextField textField() {
    final JTextField tf = new JTextField(30);
    tf.setMaximumSize(new Dimension(500, 30));
    return tf;
  }

  private static JComponent label(final String txt){
    final JLabel l = new JLabel(txt);
    l.setPreferredSize(l.getMinimumSize());
    l.setMaximumSize(new Dimension(100, 30));
    return l;
  }

}
