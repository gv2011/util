package com.github.gv2011.util;

import java.util.function.Supplier;

public interface CloseableFuture<T> extends Supplier<T>, AutoCloseableNt{

}
