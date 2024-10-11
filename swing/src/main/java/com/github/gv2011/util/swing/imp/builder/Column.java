package com.github.gv2011.util.swing.imp.builder;

final class Column extends Group<VTabImp>{

  Column(final VTabImp tab, final Block block) {
    super(tab, block);
  }

  @Override
  void setBounds(final Block block, final short upper, final short height) {
    block.setVertical(upper, height);
  }

  @Override
  Orientation orientation() {
    return Orientation.VERTICAL;
  }

}
