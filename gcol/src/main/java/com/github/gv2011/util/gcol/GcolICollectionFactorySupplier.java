package com.github.gv2011.util.gcol;

import com.github.gv2011.util.icol.ICollectionFactory;
import com.github.gv2011.util.icol.ICollectionFactorySupplier;

public final class GcolICollectionFactorySupplier implements ICollectionFactorySupplier{

  @Override
  public ICollectionFactory get() {
    return GuavaIcolFactory.INSTANCE;
  }

}
