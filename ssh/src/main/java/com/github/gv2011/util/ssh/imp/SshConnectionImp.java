package com.github.gv2011.util.ssh.imp;

import static com.github.gv2011.util.StringUtils.isTrimmed;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.bytes.ByteUtils.asUtf8;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.wrap;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;

import com.github.gv2011.util.Verify;
import com.github.gv2011.util.bytes.ByteIterator;
import com.github.gv2011.util.bytes.ByteScanner;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.RsaKeyPair;
import com.github.gv2011.util.ssh.AuthenticationFailedException;
import com.github.gv2011.util.ssh.SshConnection;
import com.github.gv2011.util.ssh.User;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.KeyType;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.schmizz.sshj.userauth.UserAuthException;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;

final class SshConnectionImp implements SshConnection{

  private static final Logger LOG = getLogger(SshConnectionImp.class);

  private static final int SSH_DEFAULT_PORT = 22;

  private final Domain host;
  private final Opt<PublicKey> hostKey;
  private final int port;
  private final SSHClient client;
  @SuppressWarnings("unused")
  private final User user;
  private OutputStream sOut;
  private InputStream sIn;
  private ByteScanner scanner;
  private final Bytes separator = asUtf8(UUID.randomUUID().toString()).content();
  private final Bytes commandTermination =
    asUtf8("\necho -n ").content().append(separator).append(asUtf8("\n").content())
  ;





  SshConnectionImp(
    final Domain host, final Opt<Integer> port, final Opt<PublicKey> hostKey, final User user, final RsaKeyPair userKey
  ) throws AuthenticationFailedException{
    this.host = host;
    this.hostKey = hostKey;
    client = new SSHClient();
    this.port = port.orElse(SSH_DEFAULT_PORT);
    try{
      this.user = user;
      client.addHostKeyVerifier(new HostKeyVerifierImp());
      call(()->client.connect(host.toString(), this.port));
      try {
        client.authPublickey(user.toString(), keyProvider(userKey));
      } catch (final UserAuthException e) {
        throw new AuthenticationFailedException(e);
      } catch (final TransportException e) {
        throw wrap(e);
      }
      final Session session = call(()->client.startSession());
      call(()->session.startShell());
      sOut = session.getOutputStream();
      sIn = session.getInputStream();
      scanner = new ByteScanner(
        ByteUtils.asIterator(sIn),
        separator.iterator()
      );
      final String prolog = execute("");
      LOG.debug("Prolog:\n", prolog);
    } catch(final Throwable t) {
      call(()->client.close());
      throw t;
    }
  }

  private class HostKeyVerifierImp implements HostKeyVerifier{

    @Override
    public boolean verify(String hostname, int port, PublicKey key) {
      verifyEqual(hostname, host.toString());
      verifyEqual(port, SshConnectionImp.this.port);
      return hostKey.map(hk->Arrays.equals(key.getEncoded(), hk.getEncoded())).orElse(true);
    }

    @Override
    public List<String> findExistingAlgorithms(String hostname, int port) {
      verifyEqual(hostname, host.toString());
      verifyEqual(port, SshConnectionImp.this.port);
      return hostKey.stream().map(k->k.getAlgorithm()).collect(toList());
    }
  }

  private String execute(final String command){
    Verify.verify(command, c->isTrimmed(c));
    return call(()->{
      asUtf8(command).content().write(sOut);
      commandTermination.write(sOut);
      sOut.flush();
      final ByteIterator response = scanner.next();
      return response.collectToString();
    });
  }

  @Override
  public void close() {
    call(()->client.close());
  }


  private static KeyProvider keyProvider(final RsaKeyPair userKey) {
    return new KeyProvider(){
      @Override
      public PrivateKey getPrivate() {return userKey.getPrivate();}
      @Override
      public PublicKey getPublic(){return userKey.getPublic();}
      @Override
      public KeyType getType() {return KeyType.RSA;}
    };
  }

}
