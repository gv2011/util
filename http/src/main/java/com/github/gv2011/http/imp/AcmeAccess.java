package com.github.gv2011.http.imp;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.icol.Path;
import com.github.gv2011.util.sec.Domain;

@FunctionalInterface
public interface AcmeAccess {

    AutoCloseableNt activate(Domain host, Path tokenPath, TypedBytes token);

}
