package com.github.gv2011.util.html.imp;

import com.github.gv2011.util.html.BlockType;
import com.github.gv2011.util.html.HtmlBuilder;
import com.github.gv2011.util.html.HtmlFactory;

public class HtmlFactoryImp implements HtmlFactory{

  @Override
  public HtmlBuilder newBuilder() {
    return new HtmlBuilderImp(this);
  }

  @Override
  public BlockType blockType(final String name) {
    return BlockTypeImp.valueOf(name);
  }

  @Override
  public BlockType table() {
    return BlockTypeImp.table;
  }

  @Override
  public BlockType tr() {
    return BlockTypeImp.tr;
  }

  @Override
  public BlockType td() {
    return BlockTypeImp.td;
  }

}
