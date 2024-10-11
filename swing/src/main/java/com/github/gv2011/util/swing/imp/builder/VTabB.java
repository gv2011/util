package com.github.gv2011.util.swing.imp.builder;

import static com.github.gv2011.util.Verify.verifyEqual;

import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.swing.VTab;

final class VTabB extends TabB<VTabB, VTabImp> implements VTab{

  VTabB(final GuiBuilderImp guiBuilder, final String name) {
    super(guiBuilder, name);
  }

  @Override
  VTabB self() {return this;}

  @Override
  VTabImp createTabImp() {
    if(previousGroups.isEmpty()) verifyEqual(this, guiBuilder.top());
    return new VTabImp(previousGroups.stream()
      .map(p->new Column(p.getKey().build(), p.getValue()))
      .collect(ICollections.toIList())
    );
  }

}
