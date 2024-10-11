package com.github.gv2011.util.swing.imp.builder;

import com.github.gv2011.util.icol.IList;

final class VTabImp extends TabImp<Column>{

  VTabImp(final IList<Column> columns) {
    super(columns);
  }

  @Override
  Orientation orientation() {
    return Orientation.VERTICAL;
  }

}
