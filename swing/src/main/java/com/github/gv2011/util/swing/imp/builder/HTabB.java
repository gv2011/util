package com.github.gv2011.util.swing.imp.builder;

import static com.github.gv2011.util.Verify.verifyEqual;

import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.swing.HTab;

final class HTabB extends TabB<HTabB, HTabImp> implements HTab{

  HTabB(final GuiBuilderImp guiBuilder, final String name) {
    super(guiBuilder, name);
  }

  HTabB(final GuiBuilderImp guiBuilder, final String name, final HTabB left, final Block block) {
    super(guiBuilder, name, left, block);
  }

  @Override
  HTabB self() {return this;}


  @Override
  HTabImp createTabImp(){
    if(previousGroups.isEmpty()) verifyEqual(this, guiBuilder.left());
    return new HTabImp(previousGroups.stream()
      .map(p->new Row(p.getKey().build(), p.getValue()))
      .collect(ICollections.toIList())
    );
  }

}
