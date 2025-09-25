package com.github.gv2011.util.ssh.imp;

import java.security.PublicKey;

import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.RsaKeyPair;
import com.github.gv2011.util.ssh.AuthenticationFailedException;
import com.github.gv2011.util.ssh.SshConnection;
import com.github.gv2011.util.ssh.User;

public final class SshConnectionFactory implements SshConnection.Factory{

  @Override
  public SshConnection connect(
    final Domain host, final Opt<Integer> port, final Opt<PublicKey> hostKey, final User user, final RsaKeyPair userKey
  ) throws AuthenticationFailedException {
    return new SshConnectionImp(host, port, hostKey, user, userKey);
  }

}
