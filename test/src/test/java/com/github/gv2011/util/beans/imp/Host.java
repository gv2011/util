package com.github.gv2011.util.beans.imp;

import java.net.URI;

import com.github.gv2011.util.beans.Bean;
import com.github.gv2011.util.beans.Computed;
import com.github.gv2011.util.beans.DefaultValue;

/**
 * Example class with computed attribute
 */
public interface Host extends Bean{

  @DefaultValue("true")
  Boolean secure();

  String host();

  @Computed
  URI url();

  public static URI url(final Host host) {
    return URI.create((host.secure()?"https://":"http://")+host.host());
  }
}
