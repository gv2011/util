package com.github.gv2011.util.swing.imp.builder;

import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.unsupported;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

final class GvLayout implements LayoutManager{

  private final GuiImp gui;


  GvLayout(final GuiImp gui) {
    this.gui = gui;
  }

  @Override
  public void layoutContainer(final Container parent) {
    verifyEqual(parent, gui.contentPane());
    gui.layout(parent.getSize());
  }

  @Override
  public void addLayoutComponent(final String name, final Component comp) {
    unsupported();
  }

  @Override
  public void removeLayoutComponent(final Component comp) {
    unsupported();
  }

  @Override
  public Dimension preferredLayoutSize(final Container parent) {
    return unsupported();
  }

  @Override
  public Dimension minimumLayoutSize(final Container parent) {
    return unsupported();
  }


}
