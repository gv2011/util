package com.github.gv2011.util.bytes;

import com.github.gv2011.util.AutoCloseableNt;

public interface CloseableBytes extends Bytes, AutoCloseableNt{

  Bytes loadInMemory();

}
