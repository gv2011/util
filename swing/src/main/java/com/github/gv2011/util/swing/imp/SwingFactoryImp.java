package com.github.gv2011.util.swing.imp;

import com.github.gv2011.util.swing.GuiBuilder;
import com.github.gv2011.util.swing.SwingFactory;
import com.github.gv2011.util.swing.imp.builder.GuiBuilderImp;

public final class SwingFactoryImp implements SwingFactory{

  @Override
  public GuiBuilder guiBuilder() {
    return new GuiBuilderImp();
  }

}
