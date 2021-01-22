package com.github.gv2011.util.html.imp;

import com.github.gv2011.util.html.HtmlBuilder;
import com.github.gv2011.util.html.HtmlFactory;

public class HtmlFactoryImp implements HtmlFactory{

  @Override
  public HtmlBuilder newBuilder() {
    return new HtmlBuilderImp();
  }

}
