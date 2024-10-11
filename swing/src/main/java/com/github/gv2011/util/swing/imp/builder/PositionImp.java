package com.github.gv2011.util.swing.imp.builder;

import com.github.gv2011.util.swing.Position;

final class PositionImp implements Position{

  private final VTabB vTab;
  private final HTabB hTab;


  PositionImp(final VTabB vTab, final HTabB hTab) {
    this.vTab = vTab;
    this.hTab = hTab;
  }


  @Override
  public VTabB vTab() {
    return vTab;
  }

  @Override
  public HTabB hTab() {
    return hTab;
  }

}
