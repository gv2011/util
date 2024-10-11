package com.github.gv2011.util.swing.imp.builder;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.github.gv2011.util.icol.Opt;

abstract class Block implements Flexible{

  abstract Opt<JComponent> component();

  void addTo(final JPanel contentPane){}

  void setHorizontal(final short left, final short width){}

  void setVertical(final short upper, final short height){}

}
