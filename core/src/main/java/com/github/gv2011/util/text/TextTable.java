package com.github.gv2011.util.text;

import com.github.gv2011.util.uc.UChars;
import com.github.gv2011.util.uc.UStr;

public class TextTable {

  public TextTable(final UStr text){
    text.split(UChars.TAB);
  }

}
