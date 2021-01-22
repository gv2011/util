package com.github.gv2011.util.gcol.net;

import java.net.InetAddress;

import com.github.gv2011.util.net.NetUtilsSpi;
import com.google.common.net.InetAddresses;

public final class GuavaNetUtilsSpi implements NetUtilsSpi{

  @Override
  public InetAddress forString(String ipString) {
    return InetAddresses.forString(ipString);
  }

  @Override
  public boolean isInetAddress(String ipString) {
    return InetAddresses.isInetAddress(ipString);
  }

}
