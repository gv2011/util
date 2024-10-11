package com.github.gv2011.util.swing.imp.builder;

final class Row extends Group<HTabImp>{

  Row(final HTabImp tab, final Block block) {
    super(tab, block);
  }

  @Override
  void setBounds(final Block block, final short left, final short width) {
    block.setHorizontal(left, width);
  }

  @Override
  Orientation orientation() {
    return Orientation.HORIZONTAL;
  }

}
