package com.github.gv2011.util.swing.imp.builder;

import com.github.gv2011.util.icol.IList;

final class HTabImp extends TabImp<Row>{

  HTabImp(final IList<Row> leftRows) {
    super(leftRows);
  }

  @Override
  Orientation orientation() {
    return Orientation.HORIZONTAL;
  }

}
