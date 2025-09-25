module com.github.gv2011.util.ssh{
  requires transitive com.github.gv2011.util;
  requires com.hierynomus.sshj;
  provides com.github.gv2011.util.ssh.SshConnection.Factory with com.github.gv2011.util.ssh.imp.SshConnectionFactory;
}