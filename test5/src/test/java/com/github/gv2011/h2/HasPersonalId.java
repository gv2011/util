package com.github.gv2011.h2;

import com.github.gv2011.util.beans.AbstractRoot;
import com.github.gv2011.util.beans.Bean;

@AbstractRoot
public interface HasPersonalId extends Bean{

  String organisation();

  Long number();

}
