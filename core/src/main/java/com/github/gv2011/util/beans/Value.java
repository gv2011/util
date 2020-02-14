package com.github.gv2011.util.beans;

import com.github.gv2011.util.Immutable;
import com.github.gv2011.util.uc.UStr;

public interface Value extends Immutable
//extends Comparable<Value> TODO
{
  UStr toStr();
}
