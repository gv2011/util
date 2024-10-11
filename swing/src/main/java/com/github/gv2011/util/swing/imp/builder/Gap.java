package com.github.gv2011.util.swing.imp.builder;

import static com.github.gv2011.util.swing.imp.builder.Orientation.HORIZONTAL;
import static com.github.gv2011.util.swing.imp.builder.Orientation.VERTICAL;

import javax.swing.JComponent;

import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.Opt;

final class Gap extends Block{

  private final IMap<Orientation,Elasticity> flexibilities;

  Gap(final short size, final Orientation rigid) {
    flexibilities = ICollections.<Orientation,Elasticity>mapBuilder()
      .put(rigid, new Elasticity(size))
      .put(rigid.other(), Elasticity.FLEX)
      .build()
    ;
  }

  Gap(final Elasticity horizontal, final Elasticity vertical) {
    flexibilities = ICollections.<Orientation,Elasticity>mapBuilder()
      .put(HORIZONTAL, horizontal)
      .put(VERTICAL,   vertical)
      .build()
    ;
  }

  @Override
  Opt<JComponent> component() {
    return Opt.empty();
  }

  @Override
  public Elasticity elasticity(final Orientation orientation) {
    return flexibilities.get(orientation);
  }

  @Override
  public void reset() {}

  @Override
  public String toString() {
    return "G("+elasticity(HORIZONTAL)+"/"+elasticity(VERTICAL)+")";
  }

}
